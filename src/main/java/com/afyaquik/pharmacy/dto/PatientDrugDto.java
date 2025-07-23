package com.afyaquik.pharmacy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDrugDto {
    private Long id;
    private Long patientVisitId;
    private Long drugId;
    private String drugName;
    private Double quantity;
    private Double price;
    private String dosageInstructions;
    private boolean dispensed;
}
