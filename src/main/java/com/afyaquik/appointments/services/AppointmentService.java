package com.afyaquik.appointments.services;

import com.afyaquik.appointments.dto.AppointmentDto;
import com.afyaquik.utils.dto.search.ListFetchDto;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    AppointmentDto create(AppointmentDto dto);
    ListFetchDto<AppointmentDto> getDoctorAppointments(Long doctorId, Pageable pageable);
    ListFetchDto<AppointmentDto> getPatientAppointments(Long patientId, Pageable pageable);
    AppointmentDto updateAppointment(Long appointmentId, AppointmentDto dto);
    AppointmentDto getAppointmentDetails(Long appointmentId);

    }
