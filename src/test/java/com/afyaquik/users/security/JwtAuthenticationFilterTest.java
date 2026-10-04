package com.afyaquik.users.security;

import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.RevokedTokenRepository;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.JwtProviderService;
import com.afyaquik.users.service.impl.JwtProviderServiceImpl;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {
    private final JwtProviderService tokens = mock(JwtProviderService.class);
    private final UsersRepository users = mock(UsersRepository.class);
    private final RevokedTokenRepository revoked = mock(RevokedTokenRepository.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokens, users, revoked);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void jwtSigningKeyMustBeConfiguredSecurely() {
        assertThrows(IllegalArgumentException.class, () -> new JwtProviderServiceImpl("short", 86400000));
    }

    @Test
    void signedTokensAreBoundToTheirBrowser() {
        var provider = new JwtProviderServiceImpl("TestOnlySigningKeyNotForProduction-2026", 86400000);
        String token = provider.generateToken("nurse", Set.of("NURSE"), "browser-one");
        assertTrue(provider.validateToken(token, "browser-one"));
        assertFalse(provider.validateToken(token, "browser-two"));
        assertFalse(provider.validateToken("invalid.token.value", "browser-one"));
        assertEquals("nurse", provider.getUserNameFromToken(token));
    }

    @Test
    void revokedTokenCannotAuthenticate() throws Exception {
        when(revoked.existsByToken("token")).thenReturn(true);
        execute();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(tokens, users);
    }

    @Test
    void tokenMustMatchRequestBrowser() throws Exception {
        execute();
        verify(tokens).validateToken("token", "test-browser");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(users);
    }

    @Test
    void disabledUserCannotAuthenticate() throws Exception {
        validToken();
        when(users.findByUsername("staff")).thenReturn(Optional.of(User.builder().enabled(false).build()));
        execute();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void validTokenAuthenticatesEnabledUser() throws Exception {
        validToken();
        when(users.findByUsername("staff")).thenReturn(Optional.of(User.builder().enabled(true).roles(Set.of()).build()));
        execute();
        assertEquals("staff", SecurityContextHolder.getContext().getAuthentication().getName());
    }

    private void validToken() {
        when(tokens.validateToken("token", "test-browser")).thenReturn(true);
        when(tokens.getUserNameFromToken("token")).thenReturn("staff");
    }

    private void execute() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users/me");
        request.setCookies(new Cookie("authToken", "token"));
        request.addHeader("User-Agent", "test-browser");
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
    }
}