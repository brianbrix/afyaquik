package com.afyaquik.users.security;

import com.afyaquik.users.dto.UserDto;
import com.afyaquik.users.dto.security.ApiPermissionDto;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.entity.security.ApiPermission;
import com.afyaquik.users.repository.*;
import com.afyaquik.users.service.impl.ApiPermissionServiceImpl;
import com.afyaquik.users.service.impl.UserServiceImpl;
import com.afyaquik.utils.mappers.users.ApiPermissionMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StaffAdministrationTest {
    private final UsersRepository users = mock(UsersRepository.class);
    private final RolesRepository roles = mock(RolesRepository.class);
    private final UserServiceImpl userService = new UserServiceImpl(users, null, null, roles, null, null, null, null);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void creationPersistsAvailabilityAndConfiguredStations() {
        authenticateAs("SUPERADMIN");
        StationRepository stations = mock(StationRepository.class);
        var encoder = mock(org.springframework.security.crypto.password.PasswordEncoder.class);
        var service = new UserServiceImpl(users, null, encoder, roles, stations, null, null, null);
        Role doctor = Role.builder().name("DOCTOR").build();
        var station = com.afyaquik.users.entity.Station.builder().id(1L).name("TRIAGE").build();
        when(roles.findByName("DOCTOR")).thenReturn(Optional.of(doctor));
        when(stations.findByName("TRIAGE")).thenReturn(Optional.of(station));
        when(encoder.encode("TestOnlyStaffPassword")).thenReturn("encoded");
        when(users.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var response = service.createUser(UserDto.builder().username("doctor").firstName("Synthetic")
                .lastName("Doctor").email("doctor@example.invalid").password("TestOnlyStaffPassword")
                .enabled(true).available(true).roles(Set.of("DOCTOR")).stations(Set.of("TRIAGE")).build());
        var saved = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(users).save(saved.capture());
        assertTrue(saved.getValue().isAvailable());
        assertEquals(Set.of(station), saved.getValue().getStations());
        assertEquals("", saved.getValue().getSecondName());
        assertEquals(Set.of("TRIAGE"), response.getStations());
    }

    @Test
    void adminCannotCreateSuperadmin() {
        authenticateAs("ADMIN");
        assertThrows(AccessDeniedException.class, () -> userService.createUser(
                UserDto.builder().roles(Set.of("SUPERADMIN")).build()));
        verifyNoInteractions(users);
    }

    @Test
    void clinicianCannotCreateStaffAccounts() {
        authenticateAs("DOCTOR");
        assertThrows(AccessDeniedException.class, () -> userService.createUser(
                UserDto.builder().roles(Set.of("DOCTOR")).build()));
        verifyNoInteractions(users);
    }

    @Test
    void adminCannotModifyExistingSuperadmin() {
        authenticateAs("ADMIN");
        Role superadmin = new Role();
        superadmin.setName("SUPERADMIN");
        User target = User.builder().roles(Set.of(superadmin)).build();
        when(users.findById(1L)).thenReturn(Optional.of(target));
        assertThrows(AccessDeniedException.class, () -> userService.updateUserDetails(1L,
                UserDto.builder().roles(Set.of("DOCTOR")).build()));
        verify(users, never()).save(any());
    }

    @Test
    void updatingPermissionsReplacesRolesInsteadOfRetainingRevokedAccess() {
        ApiPermissionRepository repository = mock(ApiPermissionRepository.class);
        ApiPermissionMapper mapper = mock(ApiPermissionMapper.class);
        ApiPermissionServiceImpl service = new ApiPermissionServiceImpl(repository, roles, mapper);
        Role doctor = new Role();
        doctor.setName("DOCTOR");
        Role nurse = new Role();
        nurse.setName("NURSE");
        ApiPermission existing = ApiPermission.builder().allowedRoles(new HashSet<>(Set.of(doctor, nurse))).build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(roles.findByName("DOCTOR")).thenReturn(Optional.of(doctor));
        service.updatePermission(ApiPermissionDto.builder().urlPattern("/api/patients/**")
                .roleNames(Set.of("DOCTOR")).enabled(true).build(), 1L);
        assertEquals(Set.of(doctor), existing.getAllowedRoles());
    }

    @Test
    void staffDirectoryExcludesDisabledAccountsAndSensitiveFields() {
        Role doctor = new Role();
        doctor.setName("DOCTOR");
        when(roles.findById(1L)).thenReturn(Optional.of(doctor));
        User active = User.builder().id(2L).username("doctor").enabled(true).email("private@example.com").build();
        User disabled = User.builder().id(3L).enabled(false).build();
        when(users.findByRolesIn(List.of(doctor))).thenReturn(List.of(active, disabled));
        var directory = userService.getUsersByRole(1L);
        assertEquals(1, directory.size());
        assertEquals(2L, directory.get(0).getId());
        assertNull(directory.get(0).getEmail());
    }

    private void authenticateAs(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "staff", null, List.of(new SimpleGrantedAuthority("ROLE_" + role))));
    }
}