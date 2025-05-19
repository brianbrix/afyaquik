package com.afyaquik.appointments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class AppointmentDto {
    private Long id;
    @NotNull(message = "Appointment date and time is required")
    private LocalDateTime appointmentDateTime;
    private String reason;
    private String status="PENDING";
    @NotNull(message = "Patient ID is required")
    private Long patientId;
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;
}
