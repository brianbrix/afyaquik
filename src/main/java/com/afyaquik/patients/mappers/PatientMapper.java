package com.afyaquik.patients.mappers;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.dtos.patient.PatientDto;
import com.afyaquik.patients.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper extends EntityMapper<Patient, PatientDto> {
    @Override
    PatientDto toDto(Patient patient);

}
