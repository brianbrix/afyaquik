package com.afyaquik.inpatient;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface InpatientAdmissionRepository extends JpaRepository<InpatientAdmission, Long> {
    @org.springframework.data.jpa.repository.Query("select admission.patientVisit.id from InpatientAdmission admission where admission.id = :id")
    Optional<Long> findVisitId(@org.springframework.data.repository.query.Param("id") Long id);
    Optional<InpatientAdmission> findByActivePatientKey(Long patientId);
    List<InpatientAdmission> findTop20ByPatientVisitIdOrderByIdDesc(Long visitId);
}