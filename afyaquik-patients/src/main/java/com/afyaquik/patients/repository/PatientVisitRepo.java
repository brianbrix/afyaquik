package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientVisit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientVisitRepo extends JpaRepository<PatientVisit, Long>{
    Page<PatientVisit> findAllByPatient(Pageable pageable, Patient patient);
}
