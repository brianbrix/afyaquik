package com.afyaquik.users.service.impl;

import com.afyaquik.users.dto.StationDto;
import com.afyaquik.users.dto.UserDto;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.repository.StationRepository;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.UserStationService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStationServiceImpl implements UserStationService {
    private final StationRepository stationRepository;
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;

    @Override
    @Transactional
    public StationDto createStation(StationDto stationDto) {
        // Check if station with the same name already exists
        if (stationRepository.findByName(stationDto.getName()).isPresent()) {
            throw new EntityExistsException("Station with name " + stationDto.getName() + " already exists");
        }

        // Create new station
        Station station = Station.builder()
                .name(stationDto.getName())
                .allowedRoles(new HashSet<>())
                .build();

        // Set allowed roles if provided
        if (stationDto.getAllowedRoles() != null && !stationDto.getAllowedRoles().isEmpty()) {
            Set<Role> roles = stationDto.getAllowedRoles().stream()
                    .map(roleName -> rolesRepository.findByName(roleName)
                            .orElseThrow(()-> new EntityNotFoundException("Role not found with name: " + roleName)))
                    .collect(Collectors.toSet());
            station.setAllowedRoles(roles);
        }

        // Save the station
        station = stationRepository.save(station);

        // Convert to DTO and return
        return toStationDto(station);
    }

    @Override
    @Transactional
    public StationDto updateStation(Long id, StationDto stationDto) {
        // Find existing station
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + id));

        // Update name if provided and different
        if (stationDto.getName() != null && !stationDto.getName().equals(station.getName())) {
            // Check if new name is already taken
            if (stationRepository.findByName(stationDto.getName()).isPresent()) {
                throw new EntityExistsException("Another station with name " + stationDto.getName() + " already exists");
            }
            station.setName(stationDto.getName());
        }

        // Update allowed roles if provided
        if (stationDto.getAllowedRoles() != null) {
            Set<Role> roles = stationDto.getAllowedRoles().stream()
                    .map(roleName -> rolesRepository.findByName(roleName)
                            .orElseThrow(()-> new EntityNotFoundException("Role not found with name: " + roleName)))
                    .collect(Collectors.toSet());

            // Clear existing roles and add new ones
            station.getAllowedRoles().clear();
            station.getAllowedRoles().addAll(roles);
        }

        // Save the updated station
        station = stationRepository.save(station);
        return toStationDto(station);
    }

    @Override
    @Transactional
    public void deleteStation(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + id));

        // Remove station from all users first to avoid constraint violations
        Set<User> users = usersRepository.findByStationsContaining(station);
        users.forEach(user -> user.getStations().remove(station));
        usersRepository.saveAll(users);

        // Now delete the station
        stationRepository.delete(station);
    }

    @Override
    @Transactional(readOnly = true)
    public StationDto getStation(Long id) {
        return stationRepository.findById(id)
                .map(this::toStationDto)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UserDto> getStationUsers(String stationName) {
        Station station = stationRepository.findByName(stationName)
                .orElseThrow(() -> new EntityNotFoundException("Station not found with name: " + stationName));

        return usersRepository.findByStationsContaining(station).stream()
                .map(this::toUserDto)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public Set<StationDto> getAllStations() {
        return stationRepository.findAll().stream()
                .map(this::toStationDto)
                .collect(Collectors.toSet());
    }

    // Helper method to convert Station to StationDto
    private StationDto toStationDto(Station station) {
        return StationDto.builder()
                .id(station.getId())
                .name(station.getName())
                .allowedRoles(station.getAllowedRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    // Helper method to convert User to UserDto
    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }
}
