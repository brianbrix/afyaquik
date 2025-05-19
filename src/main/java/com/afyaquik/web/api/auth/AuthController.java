package com.afyaquik.web.api.auth;

import com.afyaquik.dtos.user.LoginRequest;
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
    private final UserService userService;
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request) {
        return new ResponseEntity<>(Map.of("isLoggedIn", true),userService.login(request.getUsername(), request.getPassword()), HttpStatus.OK);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request,response);
        return ResponseEntity.ok(Map.of("message","Successfully logged out."));
    }
}
