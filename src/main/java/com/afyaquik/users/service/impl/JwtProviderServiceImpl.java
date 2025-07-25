package com.afyaquik.users.service.impl;

import com.afyaquik.users.service.JwtProviderService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class JwtProviderServiceImpl implements JwtProviderService {
    private final String jwtSecret = "MySuperSecretKeyForJWTGenerationThatIsLongEnough"; //TODO: to store in env
    private final long jwtExpirationMs = 86400000;//TODO: make configurable
    private final SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

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
            log.error("Unable to validate token", e);
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
            log.error("Unable to validate token", e);
            return false;
        }
    }
}
