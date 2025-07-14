package com.afyaquik.utils.mappers.patients;

import com.afyaquik.patients.entity.PatientAssignment;
import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.PatientAssignmentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientAssignmentsMapper extends EntityMapper<PatientAssignment, PatientAssignmentDto> {

    default PatientAssignmentDto toDto(PatientAssignment entity) {
        StringBuilder patientName = new StringBuilder();
        patientName.append(entity.getPatientVisit().getPatient().getFirstName());
        if (entity.getPatientVisit().getPatient().getSecondName() != null) {
            patientName.append(" ").append(entity.getPatientVisit().getPatient().getSecondName());
        }
        if (entity.getPatientVisit().getPatient().getLastName() != null) {
            patientName.append(" ").append(entity.getPatientVisit().getPatient().getLastName());
        }
        return PatientAssignmentDto.builder()
                .id(entity.getId())
                .patientVisitId(entity.getPatientVisit().getId())
                .patientName(patientName.toString())
                .attendingOfficerUserName(entity.getAttendingOfficer().getUsername())
                .assignedOfficer(entity.getAssignedOfficer().getUsername())
                .assignedOfficerId(entity.getAssignedOfficer().getId())
                .nextStation(entity.getNextStation().getName())
                .assignmentStatus(entity.getAssignmentStatus()!=null?entity.getAssignmentStatus().name():null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
