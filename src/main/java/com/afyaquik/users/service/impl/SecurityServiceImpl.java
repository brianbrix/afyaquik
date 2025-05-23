package com.afyaquik.users.service.impl;

import com.afyaquik.users.entity.RevokedToken;
import com.afyaquik.users.entity.Role;
import com.afyaquik.users.entity.User;
import com.afyaquik.users.repository.RevokedTokenRepository;
import com.afyaquik.users.repository.RolesRepository;
import com.afyaquik.users.repository.StationRepository;
import com.afyaquik.users.repository.UsersRepository;
import com.afyaquik.users.service.JwtProviderService;
import com.afyaquik.users.service.SecurityService;
import com.afyaquik.users.service.UserService;
import com.afyaquik.utils.exceptions.LoginException;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class SecurityServiceImpl implements SecurityService {
    private final RevokedTokenRepository revokedTokenRepository;

    private final JwtProviderService jwtProvider;
    private final UserService  userService;
    private final AuthenticationManager authenticationManager;
    @Override
    public HttpHeaders login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByUsername(username);
        String token = jwtProvider.generateToken(user.getUsername(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
        ResponseCookie cookie = ResponseCookie.from("authToken", token)
                .httpOnly(true)
                .secure(false) // Use true in production (with HTTPS)
                .path("/")
                .maxAge(Duration.ofDays(1))
                .sameSite("Strict")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return headers;
    }

    @Override
    public void   logout(HttpServletRequest request, HttpServletResponse response) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("authToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // (Optional) Audit or blacklist the token
        if (token != null) {
            RevokedToken revokedToken = new RevokedToken();
            revokedToken.setToken(token);
            revokedToken.setRevokedAt(Instant.now());
            revokedTokenRepository.save(revokedToken);
        }
        else
        {
            throw new IllegalArgumentException("Token not found");
        }

        ResponseCookie cookie = ResponseCookie.from("authToken", "")
                .httpOnly(true)
                .secure(false) // use HTTPS in production
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Override
    public boolean validateToken(HttpServletRequest request) {
        String token = null;

        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("authToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            throw new LoginException("User is not loggedIn");
        }
        if (revokedTokenRepository.existsByToken(token)) {
            throw new LoginException("User is not loggedIn");
        }
        if(!jwtProvider.validateToken(token)){
            throw new LoginException("User is not loggedIn");

        }
        return true;
    }


}
