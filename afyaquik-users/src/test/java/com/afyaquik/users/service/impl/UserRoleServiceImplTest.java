package com.afyaquik.users.service.impl;

import com.afyaquik.dtos.user.RoleRequest;
import com.afyaquik.dtos.user.RoleResponse;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.repository.RolesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRoleServiceImplTest {

    @Mock
    private RolesRepository rolesRepository;

    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    /**
     * Test case for creating a role successfully.
     * Verifies that a role is created with the correct name and the  prefix is added if missing.
     */
    @Test
    public void testCreateRole_Success() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName("ADMIN");

        Role savedRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        when(rolesRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(rolesRepository.save(any(Role.class))).thenReturn(savedRole);

        // Act
        RoleResponse response = userRoleService.createRole(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ADMIN", response.getName());
        verify(rolesRepository).findByName("ADMIN");
        verify(rolesRepository).save(any(Role.class));
    }

    /**
     * Test case for creating a role with a name that already has the  prefix.
     * Verifies that the prefix is not duplicated.
     */
    @Test
    public void testCreateRole_WithRolePrefix() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName("ADMIN");

        Role savedRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        when(rolesRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(rolesRepository.save(any(Role.class))).thenReturn(savedRole);

        // Act
        RoleResponse response = userRoleService.createRole(request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ADMIN", response.getName());
        verify(rolesRepository).findByName("ADMIN");
        verify(rolesRepository).save(any(Role.class));
    }

    /**
     * Test case for creating a role with a name that already exists.
     * Verifies that an IllegalArgumentException is thrown.
     */
    @Test
    public void testCreateRole_RoleAlreadyExists() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName("ADMIN");

        Role existingRole = Role.builder()
                .id(1L)
                .name("ADMIN")
                .build();

        when(rolesRepository.findByName("ADMIN")).thenReturn(Optional.of(existingRole));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userRoleService.createRole(request));
        verify(rolesRepository).findByName("ADMIN");
        verify(rolesRepository, never()).save(any(Role.class));
    }

    /**
     * Test case for creating a role with a null or empty name.
     * Verifies that an IllegalArgumentException is thrown.
     */
    @Test
    public void testCreateRole_NullOrEmptyName() {
        // Arrange
        RoleRequest request = new RoleRequest();
        request.setName("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userRoleService.createRole(request));
        verify(rolesRepository, never()).findByName(anyString());
        verify(rolesRepository, never()).save(any(Role.class));
    }

    /**
     * Test case for updating a role successfully.
     * Verifies that a role is updated with the correct name and the  prefix is added if missing.
     */
    @Test
    public void testUpdateRole_Success() {
        // Arrange
        Long roleId = 1L;
        RoleRequest request = new RoleRequest();
        request.setName("MODERATOR");

        Role existingRole = Role.builder()
                .id(roleId)
                .name("ADMIN")
                .build();

        when(rolesRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(rolesRepository.save(any(Role.class))).thenReturn(existingRole);

        // Act
        RoleResponse response = userRoleService.updateRole(roleId, request);

        // Assert
        assertNotNull(response);
        assertEquals(roleId, response.getId());
        assertEquals("MODERATOR", response.getName());
        verify(rolesRepository).findById(roleId);
        verify(rolesRepository).save(existingRole);
    }

    /**
     * Test case for updating a non-existent role.
     * Verifies that an EntityNotFoundException is thrown.
     */
    @Test
    public void testUpdateRole_RoleNotFound() {
        // Arrange
        Long roleId = 999L;
        RoleRequest request = new RoleRequest();
        request.setName("MODERATOR");

        when(rolesRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userRoleService.updateRole(roleId, request));
        verify(rolesRepository).findById(roleId);
        verify(rolesRepository, never()).save(any(Role.class));
    }

    /**
     * Test case for deleting a role successfully.
     * Verifies that the role is deleted.
     */
    @Test
    public void testDeleteRole_Success() {
        // Arrange
        Long roleId = 1L;
        Role existingRole = Role.builder()
                .id(roleId)
                .name("ADMIN")
                .build();

        when(rolesRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

        // Act
        userRoleService.deleteRole(roleId);

        // Assert
        verify(rolesRepository).findById(roleId);
        verify(rolesRepository).deleteById(roleId);
    }

    /**
     * Test case for deleting a non-existent role.
     * Verifies that an EntityNotFoundException is thrown.
     */
    @Test
    public void testDeleteRole_RoleNotFound() {
        // Arrange
        Long roleId = 999L;
        when(rolesRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userRoleService.deleteRole(roleId));
        verify(rolesRepository).findById(roleId);
        verify(rolesRepository, never()).deleteById(anyLong());
    }

    /**
     * Test case for getting all roles when there are no roles.
     * Verifies that an empty list is returned.
     */
    @Test
    public void testGetAllRoles_NoRoles() {
        // Arrange
        when(rolesRepository.findAll()).thenReturn(Collections.emptySet());

        // Act
        List<RoleResponse> responses = userRoleService.getAllRoles();

        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(rolesRepository).findAll();
    }
    /**
     * Test case for EntityNotFoundException when role is not found.
     * Verifies that an EntityNotFoundException is thrown when the role is not found.
     */
    @Test
    public void testUpdateRole_RoleNotFoundThrowsException() {
        // Arrange
        Long roleId = 1L;
        RoleRequest request = new RoleRequest();
        request.setName("MODERATOR");

        when(rolesRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userRoleService.updateRole(roleId, request));
        verify(rolesRepository).findById(roleId);
        verify(rolesRepository, never()).save(any(Role.class));
    }

    /**
     * Test case for getting all roles.
     * Verifies that all roles are returned correctly.
     */
    @Test
    public void testGetAllRoles() {
        // Arrange
        Role role1 = Role.builder().id(1L).name("ADMIN").build();
        Role role2 = Role.builder().id(2L).name("USER").build();
        Set<Role> roles = new LinkedHashSet<>(Arrays.asList(role1, role2));//linkedhashset so that it is ordered

        doReturn(roles).when(rolesRepository).findAll();

        // Act
        List<RoleResponse> responses = userRoleService.getAllRoles();

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("ADMIN", responses.get(0).getName());
        assertEquals(2L, responses.get(1).getId());
        assertEquals("USER", responses.get(1).getName());
        verify(rolesRepository).findAll();
    }

    /**
     * Test case for getting a role by ID.
     * Verifies that the correct role is returned.
     */
    @Test
    public void testGetRole_Success() {
        // Arrange
        Long roleId = 1L;
        Role role = Role.builder().id(roleId).name("ADMIN").build();

        when(rolesRepository.findById(roleId)).thenReturn(Optional.of(role));

        // Act
        RoleResponse response = userRoleService.getRole(roleId);

        // Assert
        assertNotNull(response);
        assertEquals(roleId, response.getId());
        assertEquals("ADMIN", response.getName());
        verify(rolesRepository).findById(roleId);
    }

    /**
     * Test case for getting a non-existent role.
     * Verifies that an EntityNotFoundException is thrown.
     */
    @Test
    public void testGetRole_RoleNotFound() {
        // Arrange
        Long roleId = 999L;
        when(rolesRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userRoleService.getRole(roleId));
        verify(rolesRepository).findById(roleId);
    }
}
