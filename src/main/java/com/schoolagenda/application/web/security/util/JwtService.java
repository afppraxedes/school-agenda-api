package com.schoolagenda.application.web.security.util;

import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private Long expiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        try {
            // Decodifica Base64 ou usa string direta
            byte[] keyBytes;
            try {
                keyBytes = Base64.getDecoder().decode(secret.trim());
            } catch (IllegalArgumentException e) {
                keyBytes = secret.trim().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            }

            // Garante tamanho mínimo de 64 bytes para HS512
            if (keyBytes.length < 64) {
                keyBytes = Arrays.copyOf(keyBytes, 64);
            }

            this.secretKey = Keys.hmacShaKeyFor(keyBytes);
            log.info("JwtService initialized with algorithm: {}", secretKey.getAlgorithm());

        } catch (Exception e) {
            log.error("Failed to initialize JwtService", e);
            throw new RuntimeException("JWT initialization failed", e);
        }
    }

    // Alteração no generateToken para usar o AgendaUserDetails de forma robusta
    public String generateToken(AgendaUserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", userDetails.getId());
        extraClaims.put("name", userDetails.getName());
        extraClaims.put("authorities", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            log.error("Failed to extract username from token", e);
            return null;
        }
    }

    public Long extractUserId(String token) {
        try {
            return extractAllClaims(token).get("id", Long.class);
        } catch (Exception e) {
            log.error("Failed to extract user ID from token", e);
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}