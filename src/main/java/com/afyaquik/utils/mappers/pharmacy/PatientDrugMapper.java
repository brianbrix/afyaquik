package com.afyaquik.utils.mappers.pharmacy;

import com.afyaquik.pharmacy.dto.PatientDrugDto;
import com.afyaquik.pharmacy.entity.PatientDrug;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientDrugMapper extends EntityMapper<PatientDrug, PatientDrugDto> {

    @Mapping(source = "patientVisit.id", target = "patientVisitId")
    @Mapping(source = "drug.id", target = "drugId")
    @Mapping(source = "drug.name", target = "drugName")
    PatientDrugDto toDto(PatientDrug entity);

    @Mapping(target = "patientVisit", ignore = true)
    @Mapping(target = "drug", ignore = true)
    PatientDrug toEntity(PatientDrugDto dto);
}
