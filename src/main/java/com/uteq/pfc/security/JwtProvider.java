package com.uteq.pfc.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Genera y valida JWT firmados (HS256). Cada token incluye un JTI (JWT ID)
 * unico que es la clave que se usa para la blacklist en Redis al hacer logout.
 */
@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtProvider(@Value("${app.jwt.secret}") String secret,
                        @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
    }

    public String generarToken(String email, String rol) {
        Instant ahora = Instant.now();
        Instant expira = ahora.plusMillis(expirationMs);

        return Jwts.builder()
                .id(UUID.randomUUID().toString())   // JTI
                .subject(email)
                .claim("rol", rol)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expira))
                .signWith(secretKey)
                .compact();
    }

    public Claims parsearClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extraerJti(String token) {
        return parsearClaims(token).getId();
    }

    public String extraerEmail(String token) {
        return parsearClaims(token).getSubject();
    }

    /** Tiempo restante de vida del token, usado como TTL para la blacklist. */
    public Duration tiempoRestante(String token) {
        Date expiracion = parsearClaims(token).getExpiration();
        long ms = expiracion.getTime() - System.currentTimeMillis();
        return Duration.ofMillis(Math.max(ms, 0));
    }

    public boolean esValido(String token) {
        try {
            parsearClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
