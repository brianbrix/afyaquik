package com.afyaquik.billing.service;

import com.afyaquik.billing.dto.BillPaymentDto;

import java.math.BigDecimal;
import java.util.List;

public interface BillPaymentService {

    /**
     * Create a new payment for a billing
     * @param billingId the billing id
     * @param amount the payment amount
     * @param paymentMethod the payment method
     * @param paymentReference the payment reference
     * @param notes optional notes about the payment
     * @return the created payment
     */
    BillPaymentDto createPayment(Long billingId, BigDecimal amount, String paymentMethod, String paymentReference, String notes);

    /**
     * Get a payment by id
     * @param id the payment id
     * @return the payment
     */
    BillPaymentDto getPaymentById(Long id);

    /**
     * Get all payments for a billing
     * @param billingId the billing id
     * @return list of payments
     */
    List<BillPaymentDto> getPaymentsByBillingId(Long billingId);

    /**
     * Get all payments for a patient visit
     * @param patientVisitId the patient visit id
     * @return list of payments
     */
    List<BillPaymentDto> getPaymentsByPatientVisitId(Long patientVisitId);

    /**
     * Delete a payment
     * @param id the payment id
     */
    void deletePayment(Long id);

    Receipt getReceipt(Long id);
    BillPaymentDto reversePayment(Long id, String reason);

    record Receipt(String receiptNumber, Long paymentId, Long billingId, Long visitId, Long patientId,
                   String patientName, String facilityName, java.time.OffsetDateTime paidAt, String receivedBy,
                   String currencyCode, BigDecimal amount, String paymentMethod, String paymentReference,
                   BigDecimal billTotal, BigDecimal paidToDate, BigDecimal balance, boolean historicalSnapshot,
                   boolean reversed, String reversalReason) {}
}
