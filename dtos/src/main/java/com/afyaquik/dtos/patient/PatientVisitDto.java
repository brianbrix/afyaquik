package com.afyaquik.dtos.patient;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PatientVisitDto {
    private Long id;
    private Long patientId;
    private String visitType;
    private String summaryReasonForVisit;
    private LocalDateTime visitDate;
    private LocalDateTime nextVisitDate;
    private PatientAttendingPlanDto attendingPlan;
    private TriageReportDto triageReportDto;
    private String visitStatus;
}
