package com.afyaquik.web.api.auth;

import com.afyaquik.users.dto.LoginRequest;
import com.afyaquik.users.dto.UserResponse;
import com.afyaquik.users.service.SecurityService;
import com.afyaquik.users.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final SecurityService securityService;
    private final UserService userService;

    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        return Map.of("token", token.getToken(), "headerName", token.getHeaderName());
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        HttpHeaders headers = securityService.login(request.getUsername(), request.getPassword(), httpRequest);
        UserResponse user = userService.fetchByUsername(request.getUsername());
        Map<String, Object> body = new HashMap<>();
        body.put("isLoggedIn", true);
        body.put("userId", user.getId());
        body.put("roles", user.getRoles());
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        securityService.logout(request,response);
        return ResponseEntity.ok(Map.of("message","Successfully logged out."));
    }
    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of("isValid", securityService.validateToken(request)));
    }
}
