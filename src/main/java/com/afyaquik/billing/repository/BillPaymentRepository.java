package com.afyaquik.billing.repository;

import com.afyaquik.billing.entity.BillPayment;
import com.afyaquik.billing.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

@Repository
public interface BillPaymentRepository extends JpaRepository<BillPayment, Long> {

    Optional<BillPayment> findByBillingIdAndPaymentMethodIgnoreCaseAndPaymentReferenceIgnoreCase(Long billingId, String method, String reference);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select payment from BillPayment payment where payment.id = :id")
    Optional<BillPayment> findForUpdate(@Param("id") Long id);

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
