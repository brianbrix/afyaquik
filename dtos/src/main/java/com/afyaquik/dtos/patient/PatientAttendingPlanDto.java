package com.afyaquik.dtos.patient;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PatientAttendingPlanDto {
    private Long id;
    private Long patientVisitId;
    private String patientName;
    private String attendingOfficerUserName;
    private String assignedOfficer;
    private String nextStation;
    private LocalDateTime createdAt;

}
