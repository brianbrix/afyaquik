package com.afyaquik.web.api.auth;

import com.afyaquik.users.dto.LoginRequest;
import com.afyaquik.users.service.SecurityService;
import com.afyaquik.users.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")//TODO: specify origins
public class AuthController {
    private final SecurityService securityService;
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        return new ResponseEntity<>(Map.of("isLoggedIn", true),securityService.login(request.getUsername(), request.getPassword(), httpRequest), HttpStatus.OK);
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
