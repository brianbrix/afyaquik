package com.afyaquik.doctor.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ObservationReportDto {
    private Long id;
    private Long patientVisitId;
    private String patientName;
    private Long doctorId;
    private String doctorName;
    private String createdAt;
    private String updatedAt;
    @NotEmpty(message = "At least one observation report item is required")
    private List<ObservationReportItemDto> items;
}
