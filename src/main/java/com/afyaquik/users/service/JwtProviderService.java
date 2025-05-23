package com.afyaquik.users.service;

import java.util.Set;

public interface JwtProviderService {
    String generateToken(String username, Set<String> roles);
    String getUserNameFromToken(String token);
    Set<String> getRolesFromToken(String token);
    boolean validateToken(String token);
}
