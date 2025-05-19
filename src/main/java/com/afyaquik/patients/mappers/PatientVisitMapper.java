package com.afyaquik.patients.mappers;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.dtos.patient.PatientVisitDto;
import com.afyaquik.patients.entity.PatientVisit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientVisitMapper extends EntityMapper<PatientVisit, PatientVisitDto> {
    @Override
    PatientVisitDto toDto(PatientVisit patient);
}
