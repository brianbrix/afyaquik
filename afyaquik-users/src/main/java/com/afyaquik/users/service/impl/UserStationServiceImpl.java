package com.afyaquik.users.service.impl;

import com.afyaquik.dtos.user.StationDto;
import com.afyaquik.dtos.user.UserDto;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.StationRepository;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.UserStationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserStationServiceImpl implements UserStationService {
    private final StationRepository stationRepository;
    private final UsersRepository  usersRepository;
    @Override
    public StationDto createStation(StationDto stationDto) {
        stationRepository.findByName(stationDto.getName()).ifPresent(station -> {
            throw new EntityExistsException("Station already exists");
        });
        Station station = Station.builder().name(stationDto.getName()).build();
        station = stationRepository.save(station);
        return StationDto.builder().id(station.getId()).name(station.getName()).build();
    }

    @Override
    public StationDto updateStation(Long id, StationDto stationDto) {
        Station station = stationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Station not found"));
        station.setName(stationDto.getName());
        station = stationRepository.save(station);
        return StationDto.builder().id(station.getId()).name(station.getName()).build();
    }

    @Override
    public void deleteStation(Long id) {
        stationRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("Station not found"));
        stationRepository.deleteById(id);
    }

    @Override
    public StationDto getStation(Long id) {
        return stationRepository.findById(id).map(station -> StationDto.builder().id(station.getId()).name(station.getName()).build()).orElseThrow(() -> new EntityNotFoundException("Station not found"));
    }

    @Override
    public Set<UserDto> getStationUsers(String name) {
        Set<Station> stations = new HashSet<>();
        Station station = stationRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Station not found"));
        stations.add(station);
        Set<User> users = usersRepository.findByStationsIn(stations);
        return users.stream().map(user -> UserDto.builder().id(user.getId()).username(user.getUsername()).firstName(user.getFirstName()).lastName(user.getLastName()).secondName(user.getSecondName()).email(user.getEmail()).enabled(user.isEnabled()).roles(user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet())).build()).collect(java.util.stream.Collectors.toSet());

    }

    @Override
    public Set<StationDto> getAllStations() {
        return stationRepository.findAll().stream().map(station -> StationDto.builder().id(station.getId()).name(station.getName()).build()).collect(java.util.stream.Collectors.toSet());
    }
}
