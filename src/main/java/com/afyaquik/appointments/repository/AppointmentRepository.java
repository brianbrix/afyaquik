package com.afyaquik.appointments.repository;

import com.afyaquik.appointments.entity.Appointment;
import com.afyaquik.appointments.enums.AppointmentStatus;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.users.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);
    Page<Appointment> findByPatientId(Long patientId, Pageable pageable);
    boolean existsByPatientAndAppointmentDateTime(Patient patient, LocalDateTime dateTime);
    boolean existsByDoctorAndAppointmentDateTimeAndStatusIn(User doctor, LocalDateTime dateTime, Set<AppointmentStatus> statuses);
}
