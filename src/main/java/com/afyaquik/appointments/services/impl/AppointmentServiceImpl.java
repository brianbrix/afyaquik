package com.afyaquik.appointments.services.impl;

import com.afyaquik.appointments.entity.Appointment;
import com.afyaquik.appointments.enums.AppointmentStatus;
import com.afyaquik.appointments.mappers.AppointmentMapper;
import com.afyaquik.appointments.repository.AppointmentRepository;
import com.afyaquik.appointments.services.AppointmentService;
import com.afyaquik.appointments.dto.AppointmentDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.patients.repository.PatientRepository;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.patients.entity.PatientVisit;
import com.afyaquik.patients.repository.PatientVisitRepo;
import com.afyaquik.patients.enums.VisitType;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepo;
    private final PatientRepository patientRepo;
    private final UsersRepository usersRepo;
    private final AppointmentMapper appointmentMapper;
    private final PatientVisitRepo patientVisitRepo;
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
        if (appointmentRepo.existsByDoctorAndAppointmentDateTimeAndStatusIn(doctor, appointmentDto.getAppointmentDateTime(), Set.of(AppointmentStatus.PENDING))) {
            throw new EntityExistsException("This doctor has a pending appointment for that time.");
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
        if (!dto.getAppointmentDateTime().equals(appointment.getAppointmentDateTime())) {
            if (appointmentRepo.existsByDoctorAndAppointmentDateTimeAndStatusIn(appointment.getDoctor(), dto.getAppointmentDateTime(), Set.of(AppointmentStatus.PENDING))) {
                throw new EntityExistsException("This doctor has a pending appointment for that time.");
            }
        }

        if (dto.getReason() != null) {
            appointment.setReason(dto.getReason());
        }
        if (dto.getStatus() != null) {
            appointment.setStatus(AppointmentStatus.valueOf(dto.getStatus()));
        }
        if (dto.getNotes()!=null) {
            appointment.setNotes(dto.getNotes());
        }
        if (dto.getAppointmentDateTime() != null) {
            appointment.setAppointmentDateTime(dto.getAppointmentDateTime());
        }


        Appointment updatedAppointment = appointmentRepo.save(appointment);
        return appointmentMapper.toDto(updatedAppointment);
    }

    @Override
    public AppointmentDto getAppointmentDetails(Long appointmentId) {
       return appointmentMapper.toDto(appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found")));
    }

    @Override
    @Transactional
    public void convertToVisit(Long appointmentId) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
            .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
        if (appointment.getStatus() == AppointmentStatus.COMPLETED || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Appointment status is already completed or cancelled.Cannot convert to visit.");
        }
        if (patientVisitRepo.existsByAppointment(appointment)) {
            throw new EntityExistsException("Appointment already has a visit.");
        }
        PatientVisit visit = PatientVisit.builder()
            .patient(appointment.getPatient())
            .visitType(VisitType.CONSULTATION)
            .summaryReasonForVisit(appointment.getNotes()!= null ? appointment.getNotes() :appointment.getReason())
            .visitDate(appointment.getAppointmentDateTime().toLocalDate())
                .appointment(appointment)
            .build();
        patientVisitRepo.save(visit);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepo.save(appointment);
    }
}
