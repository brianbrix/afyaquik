package com.afyaquik.utils.mappers.doctor;


import com.afyaquik.doctor.dto.ObservationItemDto;
import com.afyaquik.doctor.entity.ObservationItem;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObservationItemMapper extends EntityMapper<ObservationItem, ObservationItemDto> {
    @Override
    ObservationItemDto toDto(ObservationItem observationItem);
}
