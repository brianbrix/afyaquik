package com.afyaquik.dtos.patient;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientVisitDto {
    private Long id;
    private Long patientId;
    private String patientName;
    @NotBlank(message = "Visit type is required")
    private String visitType;
    private String summaryReasonForVisit;
    private LocalDateTime visitDate;
    private LocalDateTime nextVisitDate;
    private List<PatientAttendingPlanDto> attendingPlan;
    private TriageReportDto triageReportDto;
    private String visitStatus;

}
