package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long> {
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select patient from Patient patient where patient.id = :id")
    Optional<Patient> findForUpdate(@Param("id") Long id);
    @Query("SELECT p FROM Patient p LEFT JOIN FETCH p.patientVisit WHERE p.id = :id AND p.deleted = false")
    Optional<Patient> findByIdWithVisits(@Param("id") Long id);
}
