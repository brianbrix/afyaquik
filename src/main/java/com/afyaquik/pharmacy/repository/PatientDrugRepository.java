package com.afyaquik.pharmacy.repository;

import com.afyaquik.pharmacy.entity.PatientDrug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientDrugRepository extends JpaRepository<PatientDrug, Long> {
    List<PatientDrug> findByPatientVisitId(Long patientVisitId);
    List<PatientDrug> findByPatientVisitIdAndDispensed(Long patientVisitId, boolean dispensed);
}
