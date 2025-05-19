package com.afyaquik.utils.mappers.patients;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.PatientAttendingPlanDto;
import com.afyaquik.patients.entity.PatientAttendingPlan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientAttendingPlanMapper extends EntityMapper<PatientAttendingPlan, PatientAttendingPlanDto> {

    default PatientAttendingPlanDto  toDto(PatientAttendingPlan entity) {
        StringBuilder patientName = new StringBuilder();
        patientName.append(entity.getPatientVisit().getPatient().getFirstName());
        if (entity.getPatientVisit().getPatient().getSecondName() != null) {
            patientName.append(" ").append(entity.getPatientVisit().getPatient().getSecondName());
        }
        if (entity.getPatientVisit().getPatient().getLastName() != null) {
            patientName.append(" ").append(entity.getPatientVisit().getPatient().getLastName());
        }
        return PatientAttendingPlanDto.builder()
                .id(entity.getId())
                .patientVisitId(entity.getPatientVisit().getId())
                .patientName(patientName.toString())
                .attendingOfficerUserName(entity.getAttendingOfficer().getUsername())
                .assignedOfficer(entity.getAssignedOfficer().getUsername())
                .nextStation(entity.getNextStation().getName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
