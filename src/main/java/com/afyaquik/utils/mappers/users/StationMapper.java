package com.afyaquik.utils.mappers.users;

import com.afyaquik.utils.mappers.EntityMapper;
import com.afyaquik.users.dto.StationDto;
import com.afyaquik.users.entity.Station;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StationMapper extends EntityMapper<Station, StationDto> {
    @Override
    StationDto toDto(Station station);
}
