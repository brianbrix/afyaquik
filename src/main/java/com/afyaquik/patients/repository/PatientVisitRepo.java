package com.afyaquik.patients.repository;

import com.afyaquik.appointments.entity.Appointment;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.entity.PatientVisit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientVisitRepo extends JpaRepository<PatientVisit, Long>{
    Page<PatientVisit> findAllByPatient(Pageable pageable, Patient patient);
    boolean existsByAppointment(Appointment appointment);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select visit from PatientVisit visit where visit.id = :id")
    Optional<PatientVisit> findForUpdate(@Param("id") Long id);

}
