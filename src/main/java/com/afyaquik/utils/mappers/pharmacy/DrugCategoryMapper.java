package com.afyaquik.utils.mappers.pharmacy;

import com.afyaquik.pharmacy.dto.DrugCategoryDto;
import com.afyaquik.pharmacy.entity.DrugCategory;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DrugCategoryMapper extends EntityMapper<DrugCategory, DrugCategoryDto> {
    @Override
    DrugCategoryDto toDto(DrugCategory entity);
    DrugCategory toEntity(DrugCategoryDto dto);
}
