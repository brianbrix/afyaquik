package com.afyaquik.patients.mappers;

import com.afyaquik.core.mappers.EntityMapper;
import com.afyaquik.dtos.patient.PatientAttendingPlanDto;
import com.afyaquik.patients.entity.PatientAttendingPlan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientAttendingPlanMapper extends EntityMapper<PatientAttendingPlan, PatientAttendingPlanDto> {

    default PatientAttendingPlanDto  toDto(PatientAttendingPlan entity) {
        return PatientAttendingPlanDto.builder()
                .id(entity.getId())
                .patientVisitId(entity.getPatientVisit().getId())
                .patientName(entity.getPatientVisit().getPatient().getFirstName() + " " + entity.getPatientVisit().getPatient().getLastName())
                .attendingOfficerUserName(entity.getAttendingOfficer().getUsername())
                .assignedOfficer(entity.getAssignedOfficer().getUsername())
                .nextStation(entity.getNextStation().getName())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
