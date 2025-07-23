package com.afyaquik.web.api.users;

import com.afyaquik.users.dto.StationDto;
import com.afyaquik.users.dto.UserDto;
import com.afyaquik.users.service.UserStationService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {
    private final UserStationService userStationService;
    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<StationDto> createRole(@RequestBody StationDto stationDto) {
        return ResponseEntity.ok(userStationService.createStation(stationDto));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERADMIN','RECEPTIONIST')")
    public ResponseEntity<StationDto> getStation(@PathVariable Long id) {
        return ResponseEntity.ok(userStationService.getStation(id));
    }
    @GetMapping("/{name}/users")
    @PreAuthorize("hasAnyRole('SUPERADMIN','RECEPTIONIST','ADMIN')")
    public ResponseEntity<Set<UserDto>> getStationUsers(@PathVariable String name) {
        return ResponseEntity.ok(userStationService.getStationUsers(name));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<StationDto> updateStation(@PathVariable Long id,@RequestBody StationDto stationDto) {
        return ResponseEntity.ok(userStationService.updateStation(id, stationDto));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        userStationService.deleteStation(id);
        return ResponseEntity.ok(null);
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN','RECEPTIONIST')")
    public ResponseEntity<?> getAllStations() {
        Set<StationDto> roles = userStationService.getAllStations();
        return ResponseEntity.ok(roles);
    }
}
