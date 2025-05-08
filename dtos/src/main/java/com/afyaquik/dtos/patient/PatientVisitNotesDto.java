package com.afyaquik.dtos.patient;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientVisitNotesDto {
    private Long id;
    private Long patientVisitId;
    private String notes;
    private String createdBy;
    private String createdAt;
}
