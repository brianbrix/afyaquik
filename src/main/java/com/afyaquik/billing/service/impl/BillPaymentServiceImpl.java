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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillPaymentServiceImpl implements BillPaymentService {

    private final BillPaymentRepository billPaymentRepository;
    private final BillingRepository billingRepository;
    private final BillPaymentMapper billPaymentMapper;

    @Override
    @Transactional
    public BillPaymentDto createPayment(Long billingId, BigDecimal amount, String paymentMethod, String paymentReference, String notes) {
        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new EntityNotFoundException("Billing not found with id: " + billingId));

        // Check if amount is greater than amount due
        if (amount.compareTo(billing.getAmountDue()) > 0) {
            throw new IllegalArgumentException("Payment amount cannot be greater than amount due");
        }

        BillPayment payment = BillPayment.builder()
                .billing(billing)
                .amount(amount)
                .paymentMethod(paymentMethod)
                .paymentReference(paymentReference)
                .paymentDate(LocalDateTime.now())
                .notes(notes)
                .build();

        // Add payment to billing
        billing.addPayment(payment);

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
        BillPayment payment = billPaymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with id: " + id));

        Billing billing = payment.getBilling();

        // Remove payment from billing
        billing.removePayment(payment);

        // Save billing to update status if needed
        billingRepository.save(billing);

        // Delete payment
        billPaymentRepository.delete(payment);
    }
}
