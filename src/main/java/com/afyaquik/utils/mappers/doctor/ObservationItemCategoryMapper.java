package com.afyaquik.utils.mappers.doctor;

import com.afyaquik.doctor.dto.ObservationItemCategoryDto;
import com.afyaquik.doctor.entity.ObservationItemCategory;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObservationItemCategoryMapper extends EntityMapper<ObservationItemCategory, ObservationItemCategoryDto> {
    @Override
    ObservationItemCategoryDto toDto(ObservationItemCategory entity);
}
