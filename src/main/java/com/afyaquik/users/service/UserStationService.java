package com.afyaquik.users.service;

import com.afyaquik.users.dto.StationDto;
import com.afyaquik.users.dto.UserDto;

import java.util.Set;

public interface UserStationService {
    StationDto createStation(StationDto  stationDto);
    StationDto updateStation(Long id, StationDto stationDto);
    void deleteStation(Long id);
    StationDto getStation(Long id);
    Set<UserDto> getStationUsers(String stationName);
    Set<StationDto> getAllStations();
}
