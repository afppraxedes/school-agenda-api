//package com.schoolagenda.application.web.util;
//
//import com.schoolagenda.domain.model.User;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//@Service
//public class JwtService {
//
//    @Value("${jwt.secret}")
//    private String secretKey;
//
//    @Value("${jwt.expiration}")
//    private long jwtExpiration;
//
//    @Value("${jwt.refresh-token.expiration}")
//    private long refreshExpiration;
//
//    public String extractUsername(String token) {
//        return extractClaim(token, Claims::getSubject);
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
//    public String generateToken(UserDetails userDetails) {
//        return generateToken(new HashMap<>(), userDetails);
//    }
//
//    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
//        return buildToken(extraClaims, userDetails, jwtExpiration);
//    }
//
//    public String generateRefreshToken(UserDetails userDetails) {
//        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
//    }
//
//    // ✅ OVERLOAD para aceitar User diretamente
////    public String generateToken(User user) {
////        return generateToken(user.getEmail()); // Usa email como subject
////    }
////
////    public String generateRefreshToken(User user) {
////        return generateRefreshToken(user.getEmail()); // Usa email como subject
////    }
//
//    public boolean isTokenValid(String token, User user) {
//        final String username = extractUsername(token);
//        return (username.equals(user.getEmail())) && !isTokenExpired(token);
//    }
//
//    private String buildToken(
//            Map<String, Object> extraClaims,
//            UserDetails userDetails,
//            long expiration
//    ) {
//        return Jwts.builder()
//                .claims(extraClaims)
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(getSignInKey())
//                .compact();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractExpiration(token).before(new Date());
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    private Claims extractAllClaims(String token) {
//        return Jwts.parser()
//                .verifyWith(getSignInKey())
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    private SecretKey getSignInKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//}

package com.schoolagenda.application.web.util;

import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

//import javax.crypto.SecretKey;
import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

// 📁 service/JwtService.java (VERSÃO FINAL SIMPLES)
@Service
@Slf4j
//@RequiredArgsConstructor
public class JwtService {

    private static SecretKey secretKey;
    private static Long expiration;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private Long expirationValue;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshExpiration;

    // ✅ SINGLETON PATTERN - GARANTIR MESMA CHAVE SEMPRE
    @PostConstruct
    public void init() {
        if (secretKey == null) {
            secretKey = convertToSecretKey(secret);
            expiration = expirationValue;

            log.info("✅ JwtService initialized successfully");
            log.info("🔑 Secret key algorithm: {}", secretKey.getAlgorithm());
            log.info("⏰ Token expiration: {} ms", expiration);

            // ✅ DEBUG: MOSTRAR A CHAVE REAL (APENAS PARA DEBUG)
            log.debug("🔐 Secret key bytes (first 10): {}",
                    Arrays.copyOf(secretKey.getEncoded(), Math.min(10, secretKey.getEncoded().length)));
        }
    }

    // ✅ MÉTODO CONVERT TO SECRETKEY CONSISTENTE
    private SecretKey convertToSecretKey(String secret) {
        try {
            String cleanSecret = secret.trim();
            log.info("🔧 Converting secret to Key, original length: {}", cleanSecret.length());

            byte[] keyBytes;

            // ✅ SEMPRE USAR BASE64 DECODE (CONSISTENTE)
            try {
                keyBytes = Base64.getDecoder().decode(cleanSecret);
                log.info("🔑 Base64 decoded secret, length: {} bytes", keyBytes.length);
            } catch (IllegalArgumentException e) {
                log.warn("⚠️ Secret is not Base64, using as raw string");
                keyBytes = cleanSecret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                log.info("🔑 Raw string secret, length: {} bytes", keyBytes.length);
            }

            // ✅ GARANTIR TAMANHO MÍNIMO
            if (keyBytes.length < 64) {
                log.warn("⚠️ Secret too short ({} bytes), padding to 64 bytes", keyBytes.length);
                keyBytes = Arrays.copyOf(keyBytes, 64);
            } else if (keyBytes.length > 64) {
                log.warn("⚠️ Secret too long ({} bytes), truncating to 64 bytes", keyBytes.length);
                keyBytes = Arrays.copyOf(keyBytes, 64);
            }

            SecretKey key = Keys.hmacShaKeyFor(keyBytes);
            log.info("✅ SecretKey created successfully, algorithm: {}", key.getAlgorithm());
            return key;

        } catch (Exception e) {
            log.error("❌ ERROR creating secret key: {}", e.getMessage());
            throw new RuntimeException("Failed to create JWT secret key", e);
        }
    }

    // ✅ MÉTODO GERAR TOKEN
//    public String generateToken(UserDetails userDetails) {
//        log.info("🎯 Generating JWT token for user: {}", userDetails.getUsername());
//
//        try {
//            UserDetailsDTO detailsDTO = (userDetails instanceof UserDetailsDTO)
//                    ? (UserDetailsDTO) userDetails
//                    : convertToUserDetailsDTO(userDetails);
//
//            String token = Jwts.builder()
//                    .subject(detailsDTO.getUsername())
//                    .claim("id", detailsDTO.getId())
//                    .claim("name", detailsDTO.getName())
//                    .claim("email", detailsDTO.getEmail())
//                    .claim("roles", detailsDTO.getAuthorities().stream()
//                            .map(GrantedAuthority::getAuthority)
//                            .collect(Collectors.toList()))
//                    .issuedAt(new Date())
//                    .expiration(new Date(System.currentTimeMillis() + expiration))
//                    .signWith(secretKey) // ✅ MESMA CHAVE SEMPRE
//                    .compact();
//
//            log.info("✅ JWT token generated successfully");
//            debugToken("GENERATED", token);
//            return token;
//
//        } catch (Exception e) {
//            log.error("❌ ERROR generating JWT token: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to generate JWT token", e);
//        }
//    }

    public String generateToken(final UserDetailsDTO detailsDTO) {
        return Jwts.builder()
                .subject(detailsDTO.getUsername())
                .claim("id", detailsDTO.getId())
                .claim("name", detailsDTO.getName())
                .claim("email", detailsDTO.getEmail())
                // ✅ ENVIAR COMO 'authorities' (Spring Security espera este nome)
                .claim("authorities", detailsDTO.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority) // "DIRECTOR", "TEACHER"
                        .collect(Collectors.toList()))
                // ✅ MANTER 'roles' também para compatibilidade com Frontend
                .claim("roles", detailsDTO.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey)
                .compact();
    }

    // ✅ MÉTODO VALIDAR TOKEN
    public boolean validateToken(String token) {
        log.info("🔐 Validating JWT token...");
        try {
            Jwts.parser()
                    .verifyWith(secretKey) // ✅ MESMA CHAVE SEMPRE
                    .build()
                    .parseSignedClaims(token);
            log.info("✅ JWT token validated successfully");
            debugToken("VALIDATED", token);
            return true;
        } catch (Exception e) {
            log.error("❌ JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // ✅ MÉTODO DEBUG MELHORADO
    public void debugToken(String operation, String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            log.info("🔓 {} TOKEN PAYLOAD:", operation);
            log.info("   Subject: {}", claims.getSubject());
            log.info("   ID: {}", claims.get("id", String.class));
            log.info("   Name: {}", claims.get("name", String.class));
            log.info("   Email: {}", claims.get("email", String.class));
            log.info("   Roles: {}", claims.get("roles", List.class));
            log.info("   IAT: {}", claims.getIssuedAt());
            log.info("   EXP: {}", claims.getExpiration());

        } catch (Exception e) {
            log.error("❌ ERROR decoding {} token: {}", operation, e.getMessage());
        }
    }

    // ✅ MÉTODO PARA EXTRAIR USERNAME
    public String extractUsername(String token) {
        log.info("🔐 Extracting username from JWT token...");
        try {
            String username = getClaimsFromToken(token).getSubject();
            log.info("✅ Username extracted: {}", username);
            return username;
        } catch (Exception e) {
            log.error("❌ Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    // ✅ MÉTODO PARA EXTRAIR CLAIMS
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ MÉTODO AUXILIAR PARA CONVERSÃO
    private UserDetailsDTO convertToUserDetailsDTO(UserDetails userDetails) {
        return new UserDetailsDTO(
                "unknown",
                "unknown",
                userDetails.getUsername(),
                userDetails.getPassword(),
                userDetails.getAuthorities()
        );
    }
}

//@Service
//@Slf4j
//public class JwtService {
//
//    @Value("${jwt.secret}")
////    private String secretKey;
//    private String secretKey;
//
//    @Value("${jwt.expiration}")
//    private long expiration;
//
//    @Value("${jwt.refresh-token.expiration}")
//    private long refreshExpiration;
//
//    @Bean
//    public SecretKey jwtSecretKey() {
//        return convertToSecretKey(secretKey);
//    }
//
//    @Bean
//    public Long jwtExpiration() {
//        return expiration;
//    }
//
//    @Bean
//    public Long refreshExpiration() {
//        return refreshExpiration;
//    }
//
//    private SecretKey convertToSecretKey(String secret) {
//        try {
//            String cleanSecret = secret.trim();
//            byte[] keyBytes;
//
//            try {
//                keyBytes = Base64.getDecoder().decode(cleanSecret);
//                log.info("🔑 Using Base64 decoded secret, length: {} bytes", keyBytes.length);
//            } catch (IllegalArgumentException e) {
//                keyBytes = cleanSecret.getBytes();
//                log.info("🔑 Using raw string as secret, length: {} bytes", keyBytes.length);
//            }
//
//            if (keyBytes.length < 64) {
//                log.warn("⚠️ Secret too short ({} bytes), padding to 64 bytes", keyBytes.length);
//                keyBytes = Arrays.copyOf(keyBytes, 64);
//            }
//
//            return Keys.hmacShaKeyFor(keyBytes);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create JWT secret key", e);
//        }
//    }
//
//    // ✅ MÉTODO PARA GERAR TOKEN (USADO NO AUTH SERVICE)
//    public String generateToken(UserDetails userDetails) {
//        log.info("🎯 Generating JWT token for user: {}", userDetails.getUsername());
//
//        try {
//            // Converter para UserDetailsDTO se necessário
//            UserDetailsDTO detailsDTO = (userDetails instanceof UserDetailsDTO)
//                    ? (UserDetailsDTO) userDetails
//                    : convertToUserDetailsDTO(userDetails);
//
//            String token = Jwts.builder()
//                    .subject(detailsDTO.getUsername())
//                    .claim("id", detailsDTO.getId())
//                    .claim("name", detailsDTO.getName())
//                    .claim("email", detailsDTO.getEmail())
//                    .claim("roles", detailsDTO.getAuthorities().stream()
//                            .map(GrantedAuthority::getAuthority)
//                            .collect(Collectors.toList()))
//                    .issuedAt(new Date())
//                    .expiration(new Date(System.currentTimeMillis() + expiration))
//                    .signWith(secretKey)
//                    .compact();
//
//            log.info("✅ JWT token generated successfully");
//            debugToken(token); // Debug do token gerado
//            return token;
//
//        } catch (Exception e) {
//            log.error("❌ ERROR generating JWT token: {}", e.getMessage(), e);
//            throw new RuntimeException("Failed to generate JWT token", e);
//        }
//    }
//
//    // ✅ MÉTODO PARA VALIDAR TOKEN (USADO NO JWT FILTER)
//    public boolean validateToken(String token) {
//        log.info("🔐 Validating JWT token...");
//        try {
//            Jwts.parser()
//                    .verifyWith(secretKey)
//                    .build()
//                    .parseSignedClaims(token);
//            log.info("✅ JWT token validated successfully");
//            return true;
//        } catch (Exception e) {
//            log.error("❌ JWT token validation failed: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    // ✅ MÉTODO PARA EXTRAIR USERNAME (USADO NO JWT FILTER)
//    public String extractUsername(String token) {
//        log.info("🔐 Extracting username from JWT token...");
//        try {
//            String username = getClaimsFromToken(token).getSubject();
//            log.info("✅ Username extracted: {}", username);
//            return username;
//        } catch (Exception e) {
//            log.error("❌ Error extracting username from token: {}", e.getMessage());
//            return null;
//        }
//    }
//
//    // ✅ MÉTODO PARA EXTRAIR CLAIMS
//    private Claims getClaimsFromToken(String token) {
//        return Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    // ✅ MÉTODO PARA DEBUG
//    public void debugToken(String token) {
//        try {
//            Claims claims = getClaimsFromToken(token);
//            log.info("🔓 DECODED JWT PAYLOAD:");
//            log.info("   Subject: {}", claims.getSubject());
//            log.info("   ID: {}", claims.get("id", String.class));
//            log.info("   Name: {}", claims.get("name", String.class));
//            log.info("   Email: {}", claims.get("email", String.class));
//            log.info("   Roles: {}", claims.get("roles", List.class));
//            log.info("   IAT: {}", claims.getIssuedAt());
//            log.info("   EXP: {}", claims.getExpiration());
//        } catch (Exception e) {
//            log.error("❌ ERROR decoding token: {}", e.getMessage());
//        }
//    }
//
//    // ✅ MÉTODO AUXILIAR PARA CONVERSÃO
//    private UserDetailsDTO convertToUserDetailsDTO(UserDetails userDetails) {
//        // Implemente conforme sua estrutura
//        // Isso é um fallback caso precise converter
//        return new UserDetailsDTO(
//                "unknown", // id
//                "unknown", // name
//                userDetails.getUsername(), // email
//                userDetails.getPassword(),
//                userDetails.getAuthorities()
//        );
//    }
//}

//    public String extractUsername(String token) {
//        try {
//            String username = extractClaim(token, Claims::getSubject);
//            log.info("🔐 Extracting username from token: {}", username);
//            return username;
//        } catch (Exception e) {
//            log.error("❌ Error extracting username from token: {}", e.getMessage());
//            return null;
//        }
//    }
//
//    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
//        final Claims claims = extractAllClaims(token);
//        return claimsResolver.apply(claims);
//    }
//
////    public String generateToken(UserDetails userDetails) {
////        return generateToken(new HashMap<>(), userDetails);
////    }
////
////    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
////        return buildToken(extraClaims, userDetails, jwtExpiration);
////    }
//
//    // Se existir JwtService, ele deve ter claims também:
////    public String generateToken(UserDetails userDetails) {
////        UserDetailsDTO detailsDTO = (UserDetailsDTO) userDetails;
////
////        System.out.println("🎯 JwtService GENERATE TOKEN CALLED:");
////        System.out.println("   ID: " + detailsDTO.getId());
////        System.out.println("   Name: " + detailsDTO.getName());
////
////        return Jwts.builder()
////                .subject(detailsDTO.getUsername())
////                .claim("id", detailsDTO.getId())
////                .claim("name", detailsDTO.getName())
////                .claim("email", detailsDTO.getEmail())
////                .claim("roles", detailsDTO.getAuthorities().stream()
////                        .map(GrantedAuthority::getAuthority)
////                        .collect(Collectors.toList()))
////                .issuedAt(new Date())
////                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
////                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes())) // ✅ CORREÇÃO AQUI
////                .compact();
////    }
//
//    public String generateRefreshToken(UserDetails userDetails) {
//        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
//    }
//
//    private String buildToken(
//            Map<String, Object> extraClaims,
//            UserDetails userDetails,
//            long expiration
//    ) {
//        return Jwts.builder()
//                .claims(extraClaims)
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(getSignInKey())
//                .compact();
//    }
//
//    private Date extractExpiration(String token) {
//        return extractClaim(token, Claims::getExpiration);
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        try {
//            final String username = extractUsername(token);
//            boolean isValid = (username != null && username.equals(userDetails.getUsername())) && !isTokenExpired(token);
//            log.info("🔐 Token validation - Username match: {}, Not expired: {}, Final: {}",
//                    username != null && username.equals(userDetails.getUsername()),
//                    !isTokenExpired(token),
//                    isValid);
//            return isValid;
//        } catch (Exception e) {
//            log.error("❌ Error validating token: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    private boolean isTokenExpired(String token) {
//        try {
//            Date expiration = extractExpiration(token);
//            boolean expired = expiration.before(new Date());
//            if (expired) {
//                log.warn("❌ Token expired at: {}", expiration);
//            }
//            return expired;
//        } catch (Exception e) {
//            log.error("❌ Error checking token expiration: {}", e.getMessage());
//            return true;
//        }
//    }
//
//    private Claims extractAllClaims(String token) {
//        try {
//            log.info("🔐 Parsing JWT token...");
//            Claims claims = Jwts.parser()
//                    .verifyWith(getSignInKey())
//                    .build()
//                    .parseSignedClaims(token)
//                    .getPayload();
//            log.info("🔐 JWT Claims extracted successfully");
//            return claims;
//        } catch (Exception e) {
//            log.error("❌ Error parsing JWT token: {}", e.getMessage());
//            throw new RuntimeException("Invalid JWT token", e);
//        }
//    }
//
//    private SecretKey getSignInKey() {
//        try {
//            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//            log.info("🔐 Secret key decoded, length: {} bytes", keyBytes.length);
//            return Keys.hmacShaKeyFor(keyBytes);
//        } catch (Exception e) {
//            log.error("❌ Error decoding secret key: {}", e.getMessage());
//            throw new RuntimeException("Invalid JWT secret", e);
//        }
//    }
//}