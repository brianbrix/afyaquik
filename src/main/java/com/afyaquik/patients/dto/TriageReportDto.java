package com.afyaquik.patients.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriageReportDto {
    private Long id;
    private Long patientVisitId;
    List<TriageReportItemDto> triageReportItems;
}
