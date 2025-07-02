package com.eaglebank.feature.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {
    // Secret key should be stored securely, this is just an example
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 86400000; // 1 day

    public String generateToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }

    public UUID getUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return UUID.fromString(claims.getSubject()); // userId
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }
}