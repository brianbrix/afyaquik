package com.afyaquik.utils.mappers.patients;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.PatientVisitDto;
import com.afyaquik.patients.entity.PatientVisit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientVisitMapper extends EntityMapper<PatientVisit, PatientVisitDto> {
    @Override
    PatientVisitDto toDto(PatientVisit patient);
}
