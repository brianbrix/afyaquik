package com.afyaquik.doctor.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TreatmentPlanDto {
    private Long id;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long patientVisitId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String treatmentPlanItemName;
    private Long treatmentPlanItemId;
}
