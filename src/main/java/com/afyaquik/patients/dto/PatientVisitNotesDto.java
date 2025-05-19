package com.afyaquik.patients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientVisitNotesDto {
    private Long id;
    private Long patientVisitId;
    private String notes;
    private String createdBy;
    private String createdAt;
}
