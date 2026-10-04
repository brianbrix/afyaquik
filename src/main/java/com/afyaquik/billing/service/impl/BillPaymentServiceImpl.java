package com.afyaquik.billing.service.impl;

import com.afyaquik.billing.dto.BillPaymentDto;
import com.afyaquik.billing.entity.BillPayment;
import com.afyaquik.billing.entity.Billing;
import com.afyaquik.billing.repository.BillPaymentRepository;
import com.afyaquik.billing.repository.BillingRepository;
import com.afyaquik.billing.service.BillPaymentService;
import com.afyaquik.utils.mappers.billing.BillPaymentMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import com.afyaquik.patients.enums.Status;

@Service
@RequiredArgsConstructor
public class BillPaymentServiceImpl implements BillPaymentService {

    private final BillPaymentRepository billPaymentRepository;
    private final BillingRepository billingRepository;
    private final BillPaymentMapper billPaymentMapper;
    @Value("${app.facility.name:AfyaQuik}")
    private String facilityName = "AfyaQuik";

    @Override
    @Transactional
    public BillPaymentDto createPayment(Long billingId, BigDecimal amount, String paymentMethod, String paymentReference, String notes) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (paymentMethod == null || paymentMethod.isBlank() || paymentReference == null || paymentReference.isBlank()) {
            throw new IllegalArgumentException("Payment method and receipt or transaction reference are required");
        }
        try { amount = amount.setScale(2, RoundingMode.UNNECESSARY); }
        catch (ArithmeticException exception) { throw new IllegalArgumentException("Use at most two decimal places for payments"); }
        paymentMethod = paymentMethod.trim().toUpperCase(java.util.Locale.ROOT);
        paymentReference = paymentReference.trim();
        if (!Set.of("CASH", "MPESA", "BANK_TRANSFER", "CARD", "CHEQUE", "INSURANCE", "OTHER").contains(paymentMethod)
                || paymentReference.length() > 100 || (notes != null && notes.length() > 255)) {
            throw new IllegalArgumentException("Select a supported payment method and a reference of at most 100 characters");
        }
        Billing billing = billingRepository.findForUpdate(billingId)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + billingId));
        var existing = billPaymentRepository.findByBillingIdAndPaymentMethodIgnoreCaseAndPaymentReferenceIgnoreCase(billingId, paymentMethod, paymentReference);
        if (existing.isPresent()) {
            if (existing.get().isReversed() || existing.get().getAmount().compareTo(amount) != 0) {
                throw new IllegalArgumentException("This payment reference already exists with different details or has been reversed");
            }
            return billPaymentMapper.toDto(existing.get());
        }
        if (billing.isDeleted() || billing.getStatus() == Status.CANCELLED) throw new IllegalArgumentException("Cancelled bills cannot receive payments");
        if (billing.getCurrencyCode() == null) throw new IllegalArgumentException("Review and record this bill's currency before accepting payment");

        // Check if amount is greater than amount due
        if (amount.compareTo(billing.getAmountDue()) > 0) {
            throw new IllegalArgumentException("Payment amount cannot be greater than amount due");
        }

        BillPayment payment = BillPayment.builder()
                .billing(billing)
                .amount(amount)
                .paymentMethod(paymentMethod.trim())
                .paymentReference(paymentReference.trim())
                .paymentDate(LocalDateTime.now(ZoneId.of("Africa/Nairobi")))
                .currencyCode(billing.getCurrencyCode())
                .facilityNameSnapshot(facilityName)
                .patientNameSnapshot(java.util.stream.Stream.of(billing.getPatientVisit().getPatient().getFirstName(),
                    billing.getPatientVisit().getPatient().getSecondName(), billing.getPatientVisit().getPatient().getLastName())
                    .filter(name -> name != null && !name.isBlank()).collect(Collectors.joining(" ")))
                .receivedBy(SecurityContextHolder.getContext().getAuthentication().getName())
                .billTotalSnapshot(billing.getTotalAmount())
                .notes(notes)
                .build();

        // Add payment to billing
        billing.addPayment(payment);
        payment.setBalanceSnapshot(billing.getAmountDue());
        payment.setPaidToDateSnapshot(billing.getTotalAmount().subtract(billing.getAmountDue()));

        BillPayment savedPayment = billPaymentRepository.save(payment);
        billingRepository.save(billing); // Save billing to update status if needed

        return billPaymentMapper.toDto(savedPayment);
    }

    @Override
    public BillPaymentDto getPaymentById(Long id) {
        BillPayment payment = billPaymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));
        return billPaymentMapper.toDto(payment);
    }

    @Override
    public List<BillPaymentDto> getPaymentsByBillingId(Long billingId) {
        return billPaymentRepository.findByBillingId(billingId).stream()
                .map(billPaymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BillPaymentDto> getPaymentsByPatientVisitId(Long patientVisitId) {
        return billPaymentRepository.findByBilling_PatientVisit_Id(patientVisitId).stream()
                .map(billPaymentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        throw new AccessDeniedException("Recorded payments cannot be deleted; request an audited reversal");
    }

        @Override
        @Transactional(readOnly = true)
        public Receipt getReceipt(Long id) {
        BillPayment payment = billPaymentRepository.findById(id).filter(existing -> !existing.isDeleted())
            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        var visit = payment.getBilling().getPatientVisit();
        boolean snapshot = payment.getBalanceSnapshot() != null && payment.getCurrencyCode() != null;
        return new Receipt(String.format("AQ-%d-%010d", payment.getPaymentDate().getYear(), payment.getId()),
            payment.getId(), payment.getBilling().getId(), visit.getId(), visit.getPatient().getId(),
            snapshot ? payment.getPatientNameSnapshot() : visit.getPatient().getPatientName(),
            snapshot ? payment.getFacilityNameSnapshot() : facilityName,
            payment.getPaymentDate().atZone(ZoneId.of("Africa/Nairobi")).toOffsetDateTime(), payment.getReceivedBy(),
            payment.getCurrencyCode(), payment.getAmount(), payment.getPaymentMethod(), payment.getPaymentReference(),
            payment.getBillTotalSnapshot(), payment.getPaidToDateSnapshot(), payment.getBalanceSnapshot(), snapshot,
            payment.isReversed(), payment.getReversalReason());
        }

        @Override
        @Transactional
        public BillPaymentDto reversePayment(Long id, String reason) {
        if (reason == null || reason.trim().length() < 5 || reason.trim().length() > 255) {
            throw new IllegalArgumentException("A reversal reason of 5-255 characters is required");
        }
        BillPayment payment = billPaymentRepository.findForUpdate(id)
            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        if (payment.isReversed()) return billPaymentMapper.toDto(payment);
        Billing billing = billingRepository.findForUpdate(payment.getBilling().getId())
            .orElseThrow(() -> new EntityNotFoundException("Billing not found"));
        payment.setReversed(true);
        payment.setReversalReason(reason.trim());
        payment.setReversedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        payment.setReversedAt(LocalDateTime.now(ZoneId.of("Africa/Nairobi")));
        billing.refreshPaymentStatus();
        billPaymentRepository.save(payment);
        billingRepository.save(billing);
        return billPaymentMapper.toDto(payment);
        }
}
