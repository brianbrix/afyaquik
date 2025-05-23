package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.PatientVisitNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientVisitNotesRepository extends JpaRepository<PatientVisitNotes, Long>{
}
