package com.afyaquik.doctor.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ObservationReportDto {
    private Long id;
    @NotNull(message = "Patient visit id is required")
    private Long patientVisitId;
    private String patientName;
    @NotNull(message = "Doctor id is required")
    private Long doctorId;
    private String doctorName;
    private String station;
    private String createdAt;
    private String updatedAt;
    @NotEmpty(message = "At least one observation report item is required")
    private List<ObservationReportItemDto> items;
}
