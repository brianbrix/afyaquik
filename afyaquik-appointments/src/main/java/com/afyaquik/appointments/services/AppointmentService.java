package com.afyaquik.appointments.services;

import com.afyaquik.dtos.appointments.AppointmentDto;
import com.afyaquik.dtos.search.ListFetchDto;

import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    AppointmentDto create(AppointmentDto dto);
    ListFetchDto<AppointmentDto> getDoctorAppointments(Long doctorId, Pageable pageable);
    ListFetchDto<AppointmentDto> getPatientAppointments(Long patientId, Pageable pageable);
    AppointmentDto updateAppointment(Long appointmentId, AppointmentDto dto);

    }
