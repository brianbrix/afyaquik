package com.afyaquik.utils.mappers.patients;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.patients.dto.PatientDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.utils.mappers.users.ContactInfoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = ContactInfoMapper.class)
public interface PatientMapper extends EntityMapper<Patient, PatientDto> {
    @Override
    PatientDto toDto(Patient patient);

}
