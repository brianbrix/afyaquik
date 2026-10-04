package com.afyaquik.communication;

import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.repository.*;
import com.afyaquik.appointments.repository.AppointmentRepository;
import com.afyaquik.billing.repository.BillPaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PatientCommunicationService {
    private final PatientRepository patients;
    private final PatientVisitRepo visits;
    private final AppointmentRepository appointments;
    private final BillPaymentRepository payments;
    private final PatientMessageRepository messages;
    private final AfricaTalkingSmsGateway gateway;
    private final TransactionTemplate transactions;
    @Value("${app.facility.name:AfyaQuik}")
    private String facilityName = "AfyaQuik";
    private static final ZoneId NAIROBI = ZoneId.of("Africa/Nairobi");

    @Transactional(readOnly = true)
    public Workspace workspace(Long visitId) {
        var visit = visits.findById(visitId).filter(existing -> !existing.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        Patient patient = visit.getPatient();
        if (patient.isDeleted()) throw new EntityNotFoundException("Patient is archived");
        List<SourceOption> sources = new ArrayList<>();
        if (visit.getNextVisitDate() != null && !visit.getNextVisitDate().isBefore(LocalDate.now(NAIROBI))) {
            sources.add(new SourceOption("FOLLOW_UP", visit.getId(), "Follow-up: " + visit.getNextVisitDate()));
        }
        appointments.findTop20ByPatientIdAndAppointmentDateTimeGreaterThanEqualOrderByAppointmentDateTimeAsc(patient.getId(), LocalDateTime.now(NAIROBI))
            .stream().filter(appointment -> !appointment.isDeleted() && appointment.getStatus() != null && Set.of("SCHEDULED", "CONFIRMED").contains(appointment.getStatus().name()))
            .forEach(appointment -> sources.add(new SourceOption("APPOINTMENT", appointment.getId(), "Appointment: " + appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH)))));
        payments.findByBilling_PatientVisit_Id(visitId).stream().filter(payment -> !payment.isDeleted() && !payment.isReversed())
            .forEach(payment -> sources.add(new SourceOption("RECEIPT", payment.getId(), String.format("Receipt: AQ-%d-%010d", payment.getPaymentDate().getYear(), payment.getId()))));
        return new Workspace(patient.getId(), patient.isSmsConsent() && Objects.equals(phone(patient), patient.getSmsConsentPhone()),
                patient.getCommunicationLanguage() == null ? "EN" : patient.getCommunicationLanguage(), phone(patient), gateway.configured(),
                messages.findTop50ByPatientIdOrderByIdDesc(patient.getId()).stream().map(this::view).toList(), sources);
    }

    @Transactional
    public void preferences(Long patientId, Preferences request) {
        Patient patient = activePatient(patientId);
        if (request.language() == null || !Set.of("EN", "SW").contains(request.language())) throw new IllegalArgumentException("Choose English or Kiswahili");
        if (request.smsConsent() && (request.consentSource() == null || !Set.of("VERBAL", "WRITTEN").contains(request.consentSource()) || !phone(patient).matches("\\+[1-9]\\d{7,14}"))) {
            throw new IllegalArgumentException("Record verbal or written consent and a valid mobile contact first");
        }
        patient.setSmsConsent(request.smsConsent());
        patient.setSmsConsentPhone(request.smsConsent() ? phone(patient) : null);
        patient.setSmsConsentSource(request.smsConsent() ? request.consentSource() : "WITHDRAWN");
        patient.setSmsConsentBy(actor());
        patient.setSmsConsentAt(LocalDateTime.now(NAIROBI));
        patient.setCommunicationLanguage(request.language());
        patients.save(patient);
        if (!request.smsConsent()) messages.findByPatientIdAndStatus(patientId, "QUEUED").forEach(message -> {
            message.setStatus("CANCELLED");
            message.setFailure("Patient withdrew SMS consent");
        });
    }

    @Transactional
    public MessageView queue(Long patientId, QueueRequest request) {
        Patient patient = activePatient(patientId);
        requireConsent(patient);
        if (request.clientReference() == null || request.sourceId() == null) throw new IllegalArgumentException("A source record and client reference are required");
        String content = content(patient, request);
        var previous = messages.findByClientReference(request.clientReference());
        if (previous.isPresent()) {
            PatientMessage message = previous.get();
            if (!message.getPatient().getId().equals(patientId) || !message.getContent().equals(content) || !message.getRecipient().equals(phone(patient))) {
                throw new IllegalArgumentException("Client reference already belongs to different message details");
            }
            return view(message);
        }
        var duplicate = messages.findFirstByPatientIdAndTemplateAndSourceIdAndRecipientAndContentAndStatusIn(patientId, request.template(), request.sourceId(),
                phone(patient), content, List.of("QUEUED", "SENDING", "ACCEPTED", "UNKNOWN"));
        if (duplicate.isPresent()) return view(duplicate.get());
        PatientMessage message = new PatientMessage();
        message.setPatient(patient);
        message.setClientReference(request.clientReference());
        message.setTemplate(request.template());
        message.setSourceId(request.sourceId());
        message.setRecipient(phone(patient));
        message.setContent(content);
        message.setQueuedBy(actor());
        message.setConsentAt(patient.getSmsConsentAt());
        message.setConsentSource(patient.getSmsConsentSource());
        return view(messages.save(message));
    }

    public MessageView send(Long id) {
        if (!gateway.configured()) throw new IllegalArgumentException("Patient SMS provider is not enabled and configured");
        PatientMessage claimed = transactions.execute(transaction -> {
            Patient patient = activePatient(messages.findPatientId(id).orElseThrow(() -> new EntityNotFoundException("Message not found")));
            PatientMessage message = messages.findForUpdate(id).orElseThrow(() -> new EntityNotFoundException("Message not found"));
            if (!"QUEUED".equals(message.getStatus())) return null;
            requireConsent(patient);
            if (!phone(patient).equals(message.getRecipient())) throw new IllegalArgumentException("Patient contact changed; cancel this message and record new consent");
            if (!content(patient, new QueueRequest(message.getClientReference(), message.getTemplate(), message.getSourceId())).equals(message.getContent())) {
                throw new IllegalArgumentException("The source date or message language changed; cancel this draft and create an updated message");
            }
            message.setStatus("SENDING");
            message.setSentBy(actor());
            message.setAttemptedAt(LocalDateTime.now(NAIROBI));
            return messages.saveAndFlush(message);
        });
        if (claimed == null) return transactions.execute(transaction -> view(messages.findById(id).orElseThrow()));
        AfricaTalkingSmsGateway.Result result;
        try { result = gateway.send(claimed.getRecipient(), claimed.getContent()); }
        catch (RuntimeException exception) { result = new AfricaTalkingSmsGateway.Result(false, null, "UNKNOWN"); }
        final var outcome = result;
        return transactions.execute(transaction -> {
            PatientMessage message = messages.findForUpdate(id).orElseThrow();
            message.setStatus(outcome.accepted() ? "ACCEPTED" : "UNKNOWN".equals(outcome.failure()) ? "UNKNOWN" : "FAILED");
            message.setProviderMessageId(outcome.messageId());
            message.setFailure("UNKNOWN".equals(outcome.failure()) ? "Provider outcome unknown; reconcile before creating another send attempt" : outcome.failure());
            return view(messages.save(message));
        });
    }

    @Transactional
    public MessageView cancel(Long id) {
        activePatient(messages.findPatientId(id).orElseThrow(() -> new EntityNotFoundException("Message not found")));
        PatientMessage message = messages.findForUpdate(id).orElseThrow();
        if (!Set.of("QUEUED", "CANCELLED").contains(message.getStatus())) throw new IllegalArgumentException("A submitted message cannot be recalled");
        message.setStatus("CANCELLED");
        message.setFailure("Cancelled by " + actor());
        return view(message);
    }

    private String content(Patient patient, QueueRequest request) {
        boolean swahili = "SW".equals(patient.getCommunicationLanguage());
        String text;
        if ("FOLLOW_UP".equals(request.template())) {
            var visit = visits.findById(request.sourceId()).filter(existing -> !existing.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Visit not found"));
            if (!visit.getPatient().getId().equals(patient.getId()) || visit.getNextVisitDate() == null || visit.getNextVisitDate().isBefore(LocalDate.now(NAIROBI))) {
                throw new IllegalArgumentException("Choose this patient's visit with a current or future follow-up date");
            }
            text = (swahili ? "Ukumbusho: tarehe yako ya kurudi ni " : "Reminder: your follow-up date is ") + visit.getNextVisitDate() + ".";
        } else if ("APPOINTMENT".equals(request.template())) {
            var appointment = appointments.findById(request.sourceId()).filter(existing -> !existing.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
            if (!appointment.getPatient().getId().equals(patient.getId()) || appointment.getAppointmentDateTime().isBefore(LocalDateTime.now(NAIROBI))
                    || !Set.of("SCHEDULED", "CONFIRMED").contains(appointment.getStatus().name())) {
                throw new IllegalArgumentException("Choose this patient's upcoming active appointment");
            }
            text = (swahili ? "Ukumbusho: miadi yako ni " : "Reminder: your appointment is ") + appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH)) + " (EAT).";
        } else if ("RECEIPT".equals(request.template())) {
            var payment = payments.findById(request.sourceId()).filter(existing -> !existing.isDeleted() && !existing.isReversed()).orElseThrow(() -> new EntityNotFoundException("Active payment not found"));
            if (!payment.getBilling().getPatientVisit().getPatient().getId().equals(patient.getId())) throw new IllegalArgumentException("Payment does not belong to this patient");
            String receipt = String.format("AQ-%d-%010d", payment.getPaymentDate().getYear(), payment.getId());
            text = (swahili ? "Malipo yako yameandikwa. Stakabadhi " : "Your payment has been recorded. Receipt ") + receipt +
                    (swahili ? ". Tafadhali chukua nakala yako kituoni." : ". Please collect your copy at the facility.");
        } else throw new IllegalArgumentException("Choose an appointment, follow-up or receipt template");
        String content = facilityName + ": " + text;
        if (content.length() > 640) throw new IllegalArgumentException("Facility name is too long for this message template");
        return content;
    }

    private Patient activePatient(Long id) {
        return patients.findForUpdate(id).filter(patient -> !patient.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Patient not found"));
    }
    private String phone(Patient patient) { return patient.getContactInfo() == null || patient.getContactInfo().getPhoneNumber() == null ? "" : patient.getContactInfo().getPhoneNumber(); }
    private void requireConsent(Patient patient) {
        if (!patient.isSmsConsent() || !phone(patient).equals(patient.getSmsConsentPhone())) throw new IllegalArgumentException("Current consent for this mobile number is required");
    }
    private String actor() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
    private MessageView view(PatientMessage message) {
        return new MessageView(message.getId(), message.getTemplate(), message.getRecipient(), message.getContent(), message.getStatus(),
                message.getQueuedBy(), message.getCreatedAt(), message.getAttemptedAt(), message.getProviderMessageId(), message.getFailure());
    }
    public record Preferences(boolean smsConsent, String language, String consentSource) {}
    public record QueueRequest(UUID clientReference, String template, Long sourceId) {}
    public record MessageView(Long id, String template, String recipient, String content, String status, String queuedBy,
                              LocalDateTime createdAt, LocalDateTime attemptedAt, String providerMessageId, String failure) {}
    public record SourceOption(String template, Long id, String label) {}
    public record Workspace(Long patientId, boolean smsConsent, String language, String phoneNumber, boolean providerConfigured, List<MessageView> messages, List<SourceOption> sources) {}
}