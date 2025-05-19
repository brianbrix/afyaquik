package com.afyaquik.users.mappers;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.dtos.user.StationDto;
import com.afyaquik.users.entity.Station;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StationMapper extends EntityMapper<Station, StationDto> {
    @Override
    StationDto toDto(Station station);
}
