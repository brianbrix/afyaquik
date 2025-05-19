package com.afyaquik.utils.mappers.patients;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.PatientDto;
import com.afyaquik.patients.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper extends EntityMapper<Patient, PatientDto> {
    @Override
    PatientDto toDto(Patient patient);

}
