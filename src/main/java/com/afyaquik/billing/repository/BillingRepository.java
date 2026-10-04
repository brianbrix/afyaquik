package com.afyaquik.billing.repository;

import com.afyaquik.billing.entity.Billing;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select bill from Billing bill where bill.id = :id")
    Optional<Billing> findForUpdate(@Param("id") Long id);

    Optional<Billing> findByPatientVisit(PatientVisit patientVisit);

    List<Billing> findByStatus(Status status);

    List<Billing> findByPatientVisit_Patient_Id(Long patientId);
}
