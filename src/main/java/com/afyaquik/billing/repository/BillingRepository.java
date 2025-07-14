package com.afyaquik.billing.repository;

import com.afyaquik.billing.entity.Billing;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

    Optional<Billing> findByPatientVisit(PatientVisit patientVisit);

    List<Billing> findByStatus(Status status);

    List<Billing> findByPatientVisit_Patient_Id(Long patientId);
}
