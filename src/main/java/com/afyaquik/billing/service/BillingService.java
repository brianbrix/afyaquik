package com.afyaquik.billing.service;

import com.afyaquik.billing.dto.BillingDetailDto;
import com.afyaquik.billing.dto.BillingDto;
import com.afyaquik.patients.enums.Status;

import java.util.List;

public interface BillingService {

    /**
     * Create a new billing for a patient visit
     * @param billingDto the billing data
     * @return the created billing
     */
    BillingDto createBilling(BillingDto billingDto);

    /**
     * Update an existing billing
     * @param id the billing id
     * @param billingDto the updated billing data
     * @return the updated billing
     */
    BillingDto updateBilling(Long id, BillingDto billingDto);

    /**
     * Get a billing by id
     * @param id the billing id
     * @return the billing
     */
    BillingDto getBillingById(Long id);

    /**
     * Get a billing by patient visit id
     * @param patientVisitId the patient visit id
     * @return the billing
     */
    BillingDto getBillingByPatientVisitId(Long patientVisitId);

    /**
     * Get all billings
     * @return list of all billings
     */
    List<BillingDto> getAllBillings();

    /**
     * Get billings by status
     * @param status the billing status
     * @return list of billings with the specified status
     */
    List<BillingDto> getBillingsByStatus(Status status);

    /**
     * Get billings for a patient
     * @param patientId the patient id
     * @return list of billings for the patient
     */
    List<BillingDto> getBillingsByPatientId(Long patientId);

    /**
     * Update the status of a billing
     * @param id the billing id
     * @param status the new status
     * @return the updated billing
     */
    BillingDto updateBillingStatus(Long id, Status status);

    /**
     * Record a payment for a billing
     * @param id the billing id
     * @param paymentMethod the payment method
     * @param paymentReference the payment reference
     * @return the updated billing
     */
    BillingDto recordPayment(Long id, String paymentMethod, String paymentReference);

    /**
     * Add a billing detail to a billing
     * @param billingId the billing id
     * @param billingDetailDto the billing detail data
     * @return the updated billing
     */
    BillingDto addBillingDetail(Long billingId, BillingDetailDto billingDetailDto);

    /**
     * Update a billing detail
     * @param billingDetailId the billing detail id
     * @param billingDetailDto the updated billing detail data
     * @return the updated billing detail
     */
    BillingDetailDto updateBillingDetail(Long billingDetailId, BillingDetailDto billingDetailDto);

    /**
     * Remove a billing detail from a billing
     * @param billingDetailId the billing detail id
     */
    void removeBillingDetail(Long billingDetailId);

    /**
     * Get all billing details for a billing
     * @param billingId the billing id
     * @return list of billing details
     */
    List<BillingDetailDto> getBillingDetailsByBillingId(Long billingId);
}
