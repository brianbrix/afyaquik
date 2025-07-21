package com.afyaquik.users.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

public interface SecurityService {
    void logout(HttpServletRequest request, HttpServletResponse response);
    String getCurrentUsername();
    boolean validateToken(HttpServletRequest request);
    HttpHeaders login(String username, String password, HttpServletRequest request);

}
