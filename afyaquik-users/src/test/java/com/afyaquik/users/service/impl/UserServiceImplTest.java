package com.afyaquik.users.service.impl;

import com.afyaquik.dtos.user.UserDto;
import com.afyaquik.dtos.user.UserResponse;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.RevokedTokenRepository;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.repository.UsersRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RevokedTokenRepository revokedTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto userDto;
    private User user;
    private Role role;

    @BeforeEach
    public void setup() {
        // Setup common test data
        role = Role.builder()
                .id(1L)
                .name("USER")
                .build();

        Set<String> roleNames = new HashSet<>();
        roleNames.add("USER");

        userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setPassword("password");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setEmail("test@example.com");
        userDto.setEnabled(true);
        userDto.setRoles(roleNames);

        Set<Role> roles = new HashSet<>();
        roles.add(role);

        user = User.builder()
                .id(1L)
                .username("testuser")
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .passwordHash("hashedpassword")
                .enabled(true)
                .roles(roles)
                .build();
    }

    /**
     * Test case for creating a user successfully.
     * Verifies that a user is created with the correct details and roles.
     */
    @Test
    public void testCreateUser_Success() {
        // Arrange
        when(usersRepository.existsByUsername(anyString())).thenReturn(false);
        when(rolesRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("hashedpassword");

        // Mock the save method to set the ID and return the user
        when(usersRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Set the ID explicitly
            return savedUser;
        });

        // Act
        UserResponse response = userService.createUser(userDto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("USER"));

        verify(usersRepository).existsByUsername("testuser");
        verify(rolesRepository).findByName("USER");
        verify(passwordEncoder).encode("password");
        verify(usersRepository).save(any(User.class));
    }

    /**
     * Test case for creating a user with a username that already exists.
     * Verifies that an IllegalArgumentException is thrown.
     */
    @Test
    public void testCreateUser_UsernameAlreadyExists() {
        // Arrange
        when(usersRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
        verify(usersRepository).existsByUsername("testuser");
        verify(rolesRepository, never()).findByName(anyString());
        verify(usersRepository, never()).save(any(User.class));
    }

    /**
     * Test case for creating a user with a role that doesn't exist.
     * Verifies that an EntityNotFoundException is thrown.
     */
    @Test
    public void testCreateUser_RoleNotFound() {
        // Arrange
        when(usersRepository.existsByUsername(anyString())).thenReturn(false);
        when(rolesRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.createUser(userDto));
        verify(usersRepository).existsByUsername("testuser");
        verify(rolesRepository).findByName("USER");
        verify(usersRepository, never()).save(any(User.class));
    }

    /**
     * Test case for fetching a user by username when the user exists.
     * Verifies that the correct user details are returned.
     */
    @Test
    public void testFetchByUsername_UserExists() {
        // Arrange
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.fetchByUsername("testuser");

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("USER"));

        verify(usersRepository).findByUsername("testuser");
    }

    /**
     * Test case for fetching a user by username when the user doesn't exist.
     * Verifies that an EntityNotFoundException is thrown.
     */
    @Test
    public void testFetchByUsername_UserNotFound() {
        // Arrange
        when(usersRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.fetchByUsername("nonexistentuser"));
        verify(usersRepository).findByUsername("nonexistentuser");
    }

    /**
     * Test case for fetching a user by ID when the user exists.
     * Verifies that the correct user details are returned.
     */
    @Test
    public void testFetchById_UserExists() {
        // Arrange
        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse response = userService.fetchById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertEquals("test@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("USER"));

        verify(usersRepository).findById(1L);
    }

    /**
     * Test case for fetching a user by ID when the user doesn't exist.
     * Verifies that an EntityNotFoundException is thrown.
     */
    @Test
    public void testFetchById_UserNotFound() {
        // Arrange
        when(usersRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.fetchById(999L));
        verify(usersRepository).findById(999L);
    }

    /**
     * Test case for updating user details when the user exists.
     * Verifies that the user details are updated correctly.
     */
    @Test
    public void testUpdateUserDetails_Success() {
        // Arrange
        UserDto updateRequest = new UserDto();
        updateRequest.setUsername("updateduser");
        updateRequest.setFirstName("Updated");
        updateRequest.setLastName("User");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setEnabled(true);
        updateRequest.setRoles(Set.of("ADMIN"));

        Role adminRole = Role.builder()
                .id(2L)
                .name("ADMIN")
                .build();

        when(usersRepository.findById(1L)).thenReturn(Optional.of(user));
        when(rolesRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(usersRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserResponse response = userService.updateUserDetails(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(usersRepository).findById(1L);
        verify(rolesRepository).findByName("ADMIN");
        verify(usersRepository).save(any(User.class));
    }

    /**
     * Test case for updating user details when the user doesn't exist.
     * Verifies that an EntityNotFoundException is thrown.
     */
    @Test
    public void testUpdateUserDetails_UserNotFound() {
        // Arrange
        when(usersRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.updateUserDetails(999L, userDto));
        verify(usersRepository).findById(999L);
        verify(usersRepository, never()).save(any(User.class));
    }
}
