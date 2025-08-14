package com.afyaquik.users.config;

import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.Station;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Configuration
@RequiredArgsConstructor
public class StationDataLoader {

    private final StationRepository stationRepository;
    private final RolesRepository rolesRepository;

    // Map of station names to their corresponding role names
    private final Map<String, List<String>> stationRoleMappings = Map.of(
            "PHARMACY", List.of("PHARMACIST", "PHARMACY_TECH"),
            "LAB", List.of("LAB_TECHNICIAN", "PATHOLOGIST"),
            "TRIAGE", List.of("NURSE", "DOCTOR")
    );

    @Bean
    @Transactional
    public CommandLineRunner loadStations() {
        return args -> {
            // Process each station
            for (Map.Entry<String, List<String>> entry : stationRoleMappings.entrySet()) {
                String stationName = entry.getKey();
                List<String> roleNames = entry.getValue();
                
                // Find or create the station
                Station station = stationRepository.findByName(stationName)
                        .orElseGet(() -> {
                            Station newStation = Station.builder()
                                    .name(stationName)
                                    .build();
                            return stationRepository.save(newStation);
                        });
                
                // Get or create roles and associate them with the station
                Set<Role> rolesToAdd = new HashSet<>();
                for (String roleName : roleNames) {
                    Role role = rolesRepository.findByName(roleName)
                            .orElseGet(() -> {
                                Role newRole = Role.builder()
                                        .name(roleName)
                                        .build();
                                return rolesRepository.save(newRole);
                            });
                    rolesToAdd.add(role);
                }
                
                // Update station with roles
                station.getAllowedRoles().addAll(rolesToAdd);
                stationRepository.save(station);
                
                System.out.println("Updated station " + stationName + " with roles: " + 
                        String.join(", ", roleNames));
            }
        };
    }
}
