package com.afyaquik.utils.mappers.patients;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.patients.entity.PatientVisit;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {PatientAssignmentsMapper.class})
public abstract class PatientVisitMapper implements EntityMapper<PatientVisit, PatientVisitDto> {
    @Autowired
    PatientAssignmentsMapper patientAssignmentsMapper;
    @Override
    public PatientVisitDto toDto(PatientVisit entity) {
        return PatientVisitDto.builder()
                .id(entity.getId())
                .patientId(entity.getPatient().getId())
                .patientName(entity.getPatient().getPatientName())
                .assignments(entity.getPatientAssignments().stream().map(plan -> patientAssignmentsMapper.toDto(plan)).toList())
                .summaryReasonForVisit(entity.getSummaryReasonForVisit())
                .visitDate(entity.getVisitDate())
                .visitType(entity.getVisitType().name())
                .nextVisitDate(entity.getNextVisitDate())
                .visitStatus(entity.getVisitStatus()!=null?entity.getVisitStatus().name():null)
                .build();
    }
}
