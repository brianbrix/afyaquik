package com.afyaquik.users.service.impl;

import com.afyaquik.users.service.JwtProviderService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtProviderServiceImplTest {

    @InjectMocks
    private JwtProviderServiceImpl jwtProviderService;

    private String username;
    private Set<String> roles;
    private String token;

    @BeforeEach
    public void setup() {
        username = "testuser";
        roles = new HashSet<>();
        roles.add("ROLE_USER");
        roles.add("ROLE_ADMIN");
    }

    /**
     * Test case for generating a JWT token.
     * Verifies that a token is generated successfully and is not null or empty.
     */
    @Test
    public void testGenerateToken() {
        // Act
        token = jwtProviderService.generateToken(username, roles);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    /**
     * Test case for extracting username from a JWT token.
     * Verifies that the username extracted from the token matches the original username.
     */
    @Test
    public void testGetUserNameFromToken() {
        // Arrange
        token = jwtProviderService.generateToken(username, roles);

        // Act
        String extractedUsername = jwtProviderService.getUserNameFromToken(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    /**
     * Test case for extracting roles from a JWT token.
     * Verifies that the roles extracted from the token match the original roles.
     */
    @Test
    public void testGetRolesFromToken() {
        // Arrange
        token = jwtProviderService.generateToken(username, roles);

        // Act
        Set<String> extractedRoles = jwtProviderService.getRolesFromToken(token);

        // Assert
        assertEquals(roles.size(), extractedRoles.size());
        assertTrue(extractedRoles.containsAll(roles));
    }

    /**
     * Test case for validating a valid JWT token.
     * Verifies that a properly generated token is validated as true.
     */
    @Test
    public void testValidateToken_ValidToken() {
        // Arrange
        token = jwtProviderService.generateToken(username, roles);

        // Act
        boolean isValid = jwtProviderService.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    /**
     * Test case for validating an invalid JWT token.
     * Verifies that an invalid token is validated as false.
     */
    @Test
    public void testValidateToken_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.string";

        // Act
        boolean isValid = jwtProviderService.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    /**
     * Test case for validating a null JWT token.
     * Verifies that a null token is validated as false.
     */
    @Test
    public void testValidateToken_NullToken() {
        // Act
        boolean isValid = jwtProviderService.validateToken(null);

        // Assert
        assertFalse(isValid);
    }
}
