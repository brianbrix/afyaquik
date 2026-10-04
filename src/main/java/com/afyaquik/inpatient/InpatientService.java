package com.afyaquik.inpatient;

import com.afyaquik.patients.entity.*;
import com.afyaquik.patients.enums.Status;
import com.afyaquik.patients.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InpatientService {
    private final PatientRepository patients;
    private final PatientVisitRepo visits;
    private final InpatientBedRepository beds;
    private final InpatientAdmissionRepository admissions;

    @Transactional(readOnly = true)
    public Workspace workspace(Long visitId) {
        var visit = visits.findById(visitId).filter(existing -> !existing.isDeleted() && !existing.getPatient().isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Active visit not found"));
        return new Workspace(admissions.findTop20ByPatientVisitIdOrderByIdDesc(visitId).stream().map(this::view).toList(),
                beds.findByDeletedFalseOrderByWardAscCodeAsc().stream().map(bed -> new BedView(bed.getId(), bed.getWard(), bed.getCode(), bed.isActive(), bed.getCurrentAdmission() != null)).toList(),
                role("DOCTOR", "ADMIN", "SUPERADMIN"), role("NURSE", "ADMIN", "SUPERADMIN"), role("ADMIN", "SUPERADMIN"),
                visit.getVisitStatus() != Status.COMPLETED && visit.getVisitStatus() != Status.CANCELLED);
    }

    @Transactional
    public BedView createBed(BedRequest request) {
        require("ADMIN", "SUPERADMIN");
        InpatientBed bed = new InpatientBed();
        bed.setWard(text(request.ward(), 2, 80, "Ward"));
        bed.setCode(text(request.code(), 1, 30, "Bed code"));
        beds.save(bed);
        return new BedView(bed.getId(), bed.getWard(), bed.getCode(), true, false);
    }

    @Transactional
    public AdmissionView admit(AdmissionRequest request) {
        require("DOCTOR", "ADMIN", "SUPERADMIN");
        PatientVisit visit = visits.findForUpdate(request.visitId()).filter(existing -> !existing.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        Patient patient = patient(visit.getPatient().getId());
        if (visit.getVisitStatus() == Status.COMPLETED || visit.getVisitStatus() == Status.CANCELLED || admissions.findByActivePatientKey(patient.getId()).isPresent()) {
            throw new IllegalArgumentException("An open encounter without another active admission is required");
        }
        InpatientBed bed = bed(request.bedId());
        available(bed);
        InpatientAdmission admission = new InpatientAdmission();
        admission.setPatientVisit(visit);
        admission.setActivePatientKey(patient.getId());
        admission.setBed(bed);
        admission.setAdmittedAt(now());
        admission.setAdmittedBy(actor());
        admission.setAdmissionReason(text(request.reason(), 5, 1000, "Admission reason"));
        event(admission, "ADMITTED", null, bed, admission.getAdmissionReason());
        admissions.saveAndFlush(admission);
        bed.setCurrentAdmission(admission);
        beds.save(bed);
        visit.setInpatientActive(true);
        visit.setVisitStatus(Status.IN_PROGRESS);
        visits.save(visit);
        return view(admission);
    }

    @Transactional
    public AdmissionView transfer(Long id, TransferRequest request) {
        require("DOCTOR", "NURSE", "ADMIN", "SUPERADMIN");
        InpatientAdmission admission = active(id);
        Long fromId = admission.getBed().getId();
        if (request.bedId() == null) throw new IllegalArgumentException("Choose a destination bed");
        if (fromId.equals(request.bedId())) return view(admission);
        InpatientBed first = bed(Math.min(fromId, request.bedId()));
        InpatientBed second = bed(Math.max(fromId, request.bedId()));
        InpatientBed from = fromId.equals(first.getId()) ? first : second;
        InpatientBed to = request.bedId().equals(first.getId()) ? first : second;
        available(to);
        String reason = text(request.reason(), 5, 1000, "Transfer reason");
        event(admission, "TRANSFERRED", from, to, reason);
        from.setCurrentAdmission(null);
        beds.saveAndFlush(from);
        to.setCurrentAdmission(admission);
        beds.save(to);
        admission.setBed(to);
        return view(admissions.save(admission));
    }

    @Transactional
    public AdmissionView orderDischarge(Long id, DischargeOrder request) {
        require("DOCTOR", "ADMIN", "SUPERADMIN");
        InpatientAdmission admission = active(id);
        if (!"ADMITTED".equals(admission.getStatus())) throw new IllegalArgumentException("This admission already has a discharge order");
        admission.setDischargeSummary(text(request.summary(), 20, 4000, "Discharge summary"));
        admission.setDischargeOrderedAt(now());
        admission.setDischargeOrderedBy(actor());
        admission.setStatus("DISCHARGE_ORDERED");
        event(admission, "DISCHARGE_ORDERED", admission.getBed(), null, "Clinician discharge order recorded");
        return view(admissions.save(admission));
    }

    @Transactional
    public AdmissionView discharge(Long id) {
        require("NURSE", "ADMIN", "SUPERADMIN");
        InpatientAdmission admission = active(id);
        if (!"DISCHARGE_ORDERED".equals(admission.getStatus())) throw new IllegalArgumentException("A clinician's discharge order is required before discharge");
        InpatientBed bed = bed(admission.getBed().getId());
        bed.setCurrentAdmission(null);
        beds.save(bed);
        admission.setStatus("DISCHARGED");
        admission.setActivePatientKey(null);
        admission.setDischargedAt(now());
        admission.setDischargedBy(actor());
        event(admission, "DISCHARGED", bed, null, "Departure recorded; bed released");
        admission.getPatientVisit().setInpatientActive(false);
        visits.save(admission.getPatientVisit());
        return view(admissions.save(admission));
    }

    private InpatientAdmission active(Long id) {
        Long visitId = admissions.findVisitId(id).orElseThrow(() -> new EntityNotFoundException("Admission not found"));
        PatientVisit visit = visits.findForUpdate(visitId).filter(existing -> !existing.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Visit not found"));
        patient(visit.getPatient().getId());
        InpatientAdmission admission = admissions.findById(id).filter(existing -> !existing.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Admission not found"));
        if ("DISCHARGED".equals(admission.getStatus())) throw new IllegalArgumentException("Discharged admissions are immutable");
        if (!visit.isInpatientActive()) throw new IllegalArgumentException("The admission and encounter require reconciliation");
        return admission;
    }
    private Patient patient(Long id) { return patients.findForUpdate(id).filter(existing -> !existing.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Patient not found")); }
    private InpatientBed bed(Long id) { return beds.findForUpdate(id).filter(existing -> !existing.isDeleted()).orElseThrow(() -> new EntityNotFoundException("Bed not found")); }
    private void available(InpatientBed bed) { if (!bed.isActive() || bed.getCurrentAdmission() != null) throw new IllegalArgumentException("Choose an active unoccupied bed"); }
    private LocalDateTime now() { return LocalDateTime.now(ZoneId.of("Africa/Nairobi")); }
    private String actor() { return SecurityContextHolder.getContext().getAuthentication().getName(); }
    private boolean role(String... allowed) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream().anyMatch(authority -> Arrays.stream(allowed).anyMatch(name -> authority.getAuthority().equals("ROLE_" + name)));
    }
    private void require(String... roles) { if (!role(roles)) throw new AccessDeniedException("This inpatient action requires an authorized clinical or administrative role"); }
    private String text(String value, int minimum, int maximum, String field) {
        if (value == null || value.trim().length() < minimum || value.trim().length() > maximum) throw new IllegalArgumentException(field + " must contain " + minimum + "-" + maximum + " characters");
        return value.trim();
    }
    private void event(InpatientAdmission admission, String action, InpatientBed from, InpatientBed to, String reason) {
        admission.getEvents().add(new InpatientAdmission.Event(action, from == null ? null : from.getWard() + " / " + from.getCode(),
                to == null ? null : to.getWard() + " / " + to.getCode(), now(), actor(), reason));
    }
    private AdmissionView view(InpatientAdmission admission) {
        boolean clinical = role("DOCTOR", "NURSE", "ADMIN", "SUPERADMIN");
        return new AdmissionView(admission.getId(), admission.getStatus(), admission.getBed().getWard(), admission.getBed().getCode(),
                admission.getAdmittedAt(), clinical ? admission.getAdmissionReason() : null, clinical ? admission.getDischargeSummary() : null,
                admission.getDischargeOrderedAt(), admission.getDischargedAt(), clinical ? admission.getEvents() : List.of());
    }
    public record BedRequest(String ward, String code) {}
    public record AdmissionRequest(Long visitId, Long bedId, String reason) {}
    public record TransferRequest(Long bedId, String reason) {}
    public record DischargeOrder(String summary) {}
    public record BedView(Long id, String ward, String code, boolean active, boolean occupied) {}
    public record AdmissionView(Long id, String status, String ward, String bed, LocalDateTime admittedAt, String admissionReason,
                                String dischargeSummary, LocalDateTime dischargeOrderedAt, LocalDateTime dischargedAt, List<InpatientAdmission.Event> events) {}
    public record Workspace(List<AdmissionView> admissions, List<BedView> beds, boolean canOrder, boolean canDischarge, boolean canManageBeds, boolean visitOpen) {}
}