package com.afyaquik.doctor.dto;

import com.afyaquik.doctor.entity.TreatmentPlanItem;
import com.afyaquik.doctor.entity.TreatmentPlanReportItem;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

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
    private Long stationId;
    private String station;
    private String doctorName;
    private String treatmentPlanItemName;
    private List<TreatmentPlanReportItemDto> treatmentPlanReportItems;
}
