package com.afyaquik.core.mappers;

import com.afyaquik.dtos.patient.PatientDto;
import com.afyaquik.dtos.user.StationDto;
import com.afyaquik.patients.entity.Patient;
import com.afyaquik.users.entity.Station;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StationMapper extends EntityMapper<Station, StationDto>{
    @Override
    StationDto toDto(Station station);
}
