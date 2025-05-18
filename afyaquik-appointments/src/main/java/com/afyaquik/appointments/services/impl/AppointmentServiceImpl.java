package com.afyaquik.appointments.services.impl;

import com.afyaquik.appointments.entity.Appointment;
import com.afyaquik.appointments.enums.AppointmentStatus;
import com.afyaquik.appointments.mappers.AppointmentMapper;
import com.afyaquik.appointments.repository.AppointmentRepository;
import com.afyaquik.appointments.services.AppointmentService;
import com.afyaquik.dtos.appointments.AppointmentDto;
import com.afyaquik.dtos.search.ListFetchDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.UsersRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final UsersRepository usersRepo;
    private final AppointmentMapper appointmentMapper;
    @Override
    @Transactional
    public AppointmentDto create(AppointmentDto appointmentDto) {
        Patient patient = patientRepo.findById(appointmentDto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("Patient not found"));
        User doctor = usersRepo.findById(appointmentDto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

        if (appointmentRepo.existsByPatientAndAppointmentDateTime(patient, appointmentDto.getAppointmentDateTime())) {
            throw new EntityExistsException("Appointment already exists at that time for this patient.");
        }

        Appointment appointment = Appointment.builder()
                .appointmentDateTime(appointmentDto.getAppointmentDateTime())
                .reason(appointmentDto.getReason())
                .status(AppointmentStatus.PENDING)
                .patient(patient)
                .doctor(doctor)
                .build();


        return appointmentMapper.toDto(appointmentRepo.save(appointment));
    }

    @Override
    public ListFetchDto<AppointmentDto> getDoctorAppointments(Long doctorId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepo.findByDoctorId(doctorId, pageable);
        return ListFetchDto.<AppointmentDto>builder()
                .results(appointments.map(appointmentMapper::toDto))
                .build();
    }

    @Override
    public ListFetchDto<AppointmentDto> getPatientAppointments(Long patientId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepo.findByPatientId(patientId, pageable);
        return ListFetchDto.<AppointmentDto>builder()
                .results(appointments.map(appointmentMapper::toDto))
                .build();
    }

    @Override
    public AppointmentDto updateAppointment(Long appointmentId, AppointmentDto dto) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (dto.getReason() != null) {
            appointment.setReason(dto.getReason());
        }
        if (dto.getStatus() != null) {
            appointment.setStatus(AppointmentStatus.valueOf(dto.getStatus()));
        }
        if (dto.getAppointmentDateTime() != null) {
            appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
        }


        Appointment updatedAppointment = appointmentRepo.save(appointment);
        return appointmentMapper.toDto(updatedAppointment);
    }
}
