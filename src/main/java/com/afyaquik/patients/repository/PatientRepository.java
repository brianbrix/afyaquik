package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long> {
    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.patientVisit WHERE p.id = :id")
    Optional<Patient> findByIdWithVisits(@Param("id") Long id);
}
