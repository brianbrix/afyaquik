package com.afyaquik.appointments.repository;

import com.afyaquik.appointments.entity.Appointment;
import com.afyaquik.patients.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);
    boolean existsByPatientAndAppointmentDateTime(Patient patient, LocalDateTime dateTime);
}
