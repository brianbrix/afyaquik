package com.afyaquik.patients.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PatientAssignmentDto {
    private Long id;
    private Long patientVisitId;
    private String patientName;
    private String attendingOfficerUserName;
    private String assignedOfficer;
    private Long assignedOfficerId;
    private String nextStation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String assignmentStatus;

}
