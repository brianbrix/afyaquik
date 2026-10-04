package com.afyaquik.users.service.impl;

import com.afyaquik.users.service.JwtProviderService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class JwtProviderServiceImpl implements JwtProviderService {
    private final long jwtExpirationMs;
    private final SecretKey key;

    public JwtProviderServiceImpl(@Value("${app.jwt.secret}") String secret,
                                 @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32 || expirationMs <= 0) {
            throw new IllegalArgumentException("Configure a JWT signing secret of at least 32 bytes and a positive token lifetime");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = expirationMs;
    }

    @Override
    public String generateToken(String username, Set<String> roles, String clientId) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .claim("clientId", clientId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public String getUserNameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public Set<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        List<?> roles = (List<?>) claims.get("roles");
        return roles
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Override
    public String getClientIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("clientId", String.class);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT validation failed: {}", e.getClass().getSimpleName());
            return false;
        }
    }

    @Override
    public boolean validateToken(String token, String clientId) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Verify that the client ID in the token matches the provided client ID
            String tokenClientId = claims.get("clientId", String.class);
            return tokenClientId != null && tokenClientId.equals(clientId);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT validation failed: {}", e.getClass().getSimpleName());
            return false;
        }
    }
}
