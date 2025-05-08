package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.PatientVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientVisitRepo extends JpaRepository<PatientVisit, Long>{
}
