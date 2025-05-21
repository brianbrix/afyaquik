package com.afyaquik.utils.mappers.patients;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.patients.entity.PatientVisit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientVisitMapper extends EntityMapper<PatientVisit, PatientVisitDto> {
    @Override
    default PatientVisitDto toDto(PatientVisit entity) {
        return PatientVisitDto.builder()
                .id(entity.getId())
                .patientId(entity.getPatient().getId())
                .patientName(entity.getPatient().getPatientName())
                .summaryReasonForVisit(entity.getSummaryReasonForVisit())
                .visitDate(entity.getVisitDate())
                .visitType(entity.getVisitType().name())
                .nextVisitDate(entity.getNextVisitDate())
                .visitStatus(entity.getVisitStatus()!=null?entity.getVisitStatus().name():null)
                .build();
    }
}
