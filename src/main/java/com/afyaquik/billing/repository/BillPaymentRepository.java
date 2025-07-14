package com.afyaquik.billing.repository;

import com.afyaquik.billing.entity.BillPayment;
import com.afyaquik.billing.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {

    /**
     * Find all payments for a specific billing
     * @param billing the billing
     * @return list of payments
     */
    List<BillPayment> findByBilling(Billing billing);

    /**
     * Find all payments for a specific billing ID
     * @param billingId the billing ID
     * @return list of payments
     */
    List<BillPayment> findByBillingId(Long billingId);

    /**
     * Find all payments for a specific patient visit
     * @param patientVisitId the patient visit ID
     * @return list of payments
     */
    List<BillPayment> findByBilling_PatientVisit_Id(Long patientVisitId);
}
