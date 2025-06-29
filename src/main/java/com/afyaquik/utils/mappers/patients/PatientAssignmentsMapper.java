package com.afyaquik.utils.mappers.patients;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.PatientAssignmentsDto;
import com.afyaquik.patients.entity.PatientAssignments;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientAssignmentsMapper extends EntityMapper<PatientAssignments, PatientAssignmentsDto> {

    default PatientAssignmentsDto  toDto(PatientAssignments entity) {
        StringBuilder patientName = new StringBuilder();
        patientName.append(entity.getPatientVisit().getPatient().getFirstName());
        if (entity.getPatientVisit().getPatient().getSecondName() != null) {
            patientName.append(" ").append(entity.getPatientVisit().getPatient().getSecondName());
        }
        if (entity.getPatientVisit().getPatient().getLastName() != null) {
            patientName.append(" ").append(entity.getPatientVisit().getPatient().getLastName());
        }
        return PatientAssignmentsDto.builder()
                .id(entity.getId())
                .patientVisitId(entity.getPatientVisit().getId())
                .patientName(patientName.toString())
                .attendingOfficerUserName(entity.getAttendingOfficer().getUsername())
                .assignedOfficer(entity.getAssignedOfficer().getUsername())
                .assignedOfficerId(entity.getAssignedOfficer().getId())
                .nextStation(entity.getNextStation().getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
