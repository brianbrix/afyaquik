package com.afyaquik.utils.mappers.doctor;


import com.afyaquik.doctor.dto.ObservationItemDto;
import com.afyaquik.doctor.entity.ObservationItem;
import com.afyaquik.utils.mappers.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ObservationItemMapper extends EntityMapper<ObservationItem, ObservationItemDto> {
    default ObservationItemDto toDto(ObservationItem entity) {
        return ObservationItemDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory().getId())
                .categoryName(entity.getCategory().getName())
                .station(entity.getStation().getId())
                .mandatory(entity.isMandatory())
                .description(entity.getDescription())
                .stationName(entity.getStation().getName())
                .price(entity.getPrice())
                .station(entity.getStation().getId())
                .build();
    }
}
