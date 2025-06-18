package com.afyaquik.utils.mappers.pharmacy;

import com.afyaquik.pharmacy.dto.DrugFormDto;
import com.afyaquik.pharmacy.entity.DrugForm;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DrugFormMapper extends EntityMapper<DrugForm, DrugFormDto> {
    DrugFormDto toDto(DrugForm entity);
    DrugForm toEntity(DrugFormDto dto);
}
