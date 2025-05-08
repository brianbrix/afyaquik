package com.afyaquik.dtos.patient;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientAttendingPlanDto {
    private Long id;
    private Long patientVisitId;
    private String attendingOfficerUserName;
    private String role;
    private String assignedOfficer;
    private String assignedOfficerRole;

}
