package com.afyaquik.users.security;

import com.afyaquik.users.dto.security.ApiPermissionDto;
import com.afyaquik.users.service.ApiPermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiAuthorizationManagerTest {
    private final ApiPermissionService permissions = mock(ApiPermissionService.class);
    private final ApiAuthorizationManager manager = new ApiAuthorizationManager(permissions);

    @BeforeEach
    void setUp() {
        when(permissions.getEnabledPermissions()).thenReturn(List.of());
    }

    @ParameterizedTest
    @CsvSource({
            "GET, /api/patients/1, RECEPTIONIST, true",
            "POST, /api/patients, CASHIER, false",
            "GET, /api/patient/visits/1, PHARMACIST, true",
            "POST, /api/observations/add, DOCTOR, true",
            "POST, /api/observations/add, RECEPTIONIST, false",
            "GET, /api/drugs, DOCTOR, true",
            "POST, /api/drugs, DOCTOR, false",
            "PUT, /api/patient-drugs/1/dispense, PHARMACIST, true",
            "PUT, /api/patient-drugs/1/dispense, DOCTOR, false",
            "POST, /api/patient-drugs/bulk, DOCTOR, true",
            "POST, /api/patient-drugs/bulk, PHARMACIST, false",
            "GET, /api/billing/1, CASHIER, true",
            "POST, /api/billing/items, CASHIER, false",
            "POST, /api/delete, RECEPTIONIST, false",
            "POST, /api/roles, ADMIN, false",
            "POST, /api/roles, SUPERADMIN, true",
            "GET, /api/admin/permissions/1, ADMIN, false",
            "GET, /api/unclassified, SUPERADMIN, false"
    })
    void enforcesWorkflowBoundaries(String method, String path, String role, boolean expected) {
        assertEquals(expected, allowed(method, path, role));
    }

    @Test
    void permissionChangesApplyWithoutRestart() {
        assertTrue(allowed("GET", "/api/patients/1", "NURSE"));
        when(permissions.getEnabledPermissions()).thenReturn(List.of(permission("/api/patients/**", Set.of("DOCTOR"))));
        assertFalse(allowed("GET", "/api/patients/1", "NURSE"));
    }

    @Test
    void mostSpecificPermissionWinsRegardlessOfDatabaseOrder() {
        when(permissions.getEnabledPermissions()).thenReturn(List.of(
                permission("/api/**", Set.of("NURSE")),
                permission("/api/patients/**", Set.of("DOCTOR"))));
        assertFalse(allowed("GET", "/api/patients/1", "NURSE"));
    }

    @Test
    void configuredPermissionsCannotGrantPrescribingToCashiers() {
        when(permissions.getEnabledPermissions()).thenReturn(List.of(permission("/api/**", Set.of("CASHIER"))));
        assertFalse(allowed("POST", "/api/patient-drugs", "CASHIER"));
    }

    @Test
    void permissionWithNoRolesDeniesAccess() {
        when(permissions.getEnabledPermissions()).thenReturn(List.of(permission("/api/patients/**", Set.of())));
        assertFalse(allowed("GET", "/api/patients/1", "DOCTOR"));
    }

    @Test
    void unauthenticatedRequestIsDenied() {
        assertFalse(manager.check(() -> null,
                new RequestAuthorizationContext(new MockHttpServletRequest("GET", "/api/patients"))).isGranted());
    }

    private boolean allowed(String method, String path, String role) {
        var authentication = new UsernamePasswordAuthenticationToken("staff", null,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        return manager.check(() -> authentication,
                new RequestAuthorizationContext(new MockHttpServletRequest(method, path))).isGranted();
    }

    private ApiPermissionDto permission(String path, Set<String> roles) {
        return ApiPermissionDto.builder().urlPattern(path).roleNames(roles).enabled(true).build();
    }
}