package com.vivitasol.projectbackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // ⚠️ IMPORTANTE: En producción usar variable de entorno
    private static final String SECRET = "MiClaveSecretaSuperSegura2025ParaJWTConMinimo256BitsDeSeguridad";
    
    // Token expira en 24 horas
    private static final long EXPIRATION_TIME = 86400000; // 24h en milisegundos

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT con claims personalizados
     */
    public String generateToken(String email, Long userId, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("rol", rol);
        claims.put("email", email);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae el email (subject) del token
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrae el userId del token
     */
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    /**
     * Extrae el rol del token
     */
    public String extractRol(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }

    /**
     * Extrae todos los claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida si el token ha expirado
     */
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Valida el token comparando email y verificando expiración
     */
    public boolean validateToken(String token, String email) {
        try {
            final String extractedEmail = extractEmail(token);
            return (extractedEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}
