package com.afyaquik.users.service.impl;

import com.afyaquik.users.service.JwtProviderService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
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
    public String generateToken(String username, Set<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
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
        return ((Set<?>) claims.get("roles"))
                .stream()
                .map(Object::toString)
                .collect(Collectors.toSet());
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
}
