package com.afyaquik.doctor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentPlanReportItemDto {
    private Long id;
    private Long treatmentPlanId;
    private Long treatmentPlanItemId;
    private String treatmentPlanItemName;
    private String reportDetails;
}
