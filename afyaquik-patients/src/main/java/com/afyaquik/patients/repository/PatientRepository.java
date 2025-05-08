package com.afyaquik.patients.repository;

import com.afyaquik.patients.entity.Patient;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
}
