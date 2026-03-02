package com.school.auth.security;

import com.school.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiry-ms}")
    private long expiryMs;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());
        claims.put("profileId", user.getProfileId());
        if (user.getClassId() != null) claims.put("classId", user.getClassId().toString());
        if (user.getSubjectIds() != null) claims.put("subjectIds", user.getSubjectIds());

        return Jwts.builder()
            .claims(claims)
            .subject(user.getId().toString())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiryMs))
            .signWith(getSignKey())
            .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
