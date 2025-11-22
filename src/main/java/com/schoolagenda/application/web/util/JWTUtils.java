package com.schoolagenda.application.web.util;

import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
import com.schoolagenda.domain.model.UserRole;
import com.schoolagenda.domain.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JWTUtils {

    private final SecretKey secretKey;
    private final Long expiration;
    private final UserRepository userRepository;

    public JWTUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expiration}") Long expiration,
                    UserRepository userRepository) {

        System.out.println("🚀 JWTUtils initialized");
        System.out.println("🔐 Secret length: " + secret.length());

        // ✅ SOLUÇÃO SIMPLIFICADA: Sempre decodifica como Base64
        this.secretKey = decodeSecretKey(secret);
        this.expiration = expiration;
        this.userRepository = userRepository;

        System.out.println("✅ JWTUtils ready");
    }

    // ✅ MÉTODO ÚNICO E CONSISTENTE para decodificar a chave
    private SecretKey decodeSecretKey(String secret) {
        try {
            // Remove possíveis espaços
            String cleanSecret = secret.trim();

            // ✅ SEMPRE trata como Base64 (seu secret é Base64)
            byte[] keyBytes = Base64.getDecoder().decode(cleanSecret);
            System.out.println("🔑 Key bytes length: " + keyBytes.length);

            return Keys.hmacShaKeyFor(keyBytes);

        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR: Invalid Base64 secret");
            System.out.println("🔑 Secret: " + secret);
            throw new RuntimeException("JWT secret must be valid Base64 encoded string", e);
        }
    }

    public String generateToken(final UserDetailsDTO detailsDTO) {
        return Jwts.builder()
                .claim("id", detailsDTO.getId())
                .claim("name", detailsDTO.getName())
                .claim("authorities", detailsDTO.getAuthorities())
                .subject(detailsDTO.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("❌ Token validation failed: " + e.getMessage());
            return false;
        }
    }

    public String getUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public <T> T getClaimFromToken(String token, String claimName, Class<T> clazz) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(claimName, clazz);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ... outros métodos permanecem iguais
    public String generateToken(String username) {
        UserDetailsDTO userDetails = getUserDetailsByUsername(username);
        return generateToken(userDetails);
    }

    private UserDetailsDTO getUserDetailsByUsername(String username) {
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Collection<? extends GrantedAuthority> authorities = convertRolesToAuthorities(user.getRoles());

        return UserDetailsDTO.create(
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

    private Collection<? extends GrantedAuthority> convertRolesToAuthorities(Set<UserRole> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }
}

//package com.schoolagenda.application.web.util;
//
//import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
//import com.schoolagenda.domain.model.UserRole;
//import com.schoolagenda.domain.repository.UserRepository;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.util.Collection;
//import java.util.Date;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class JWTUtils {
//
//    private final SecretKey secretKey;
//    private final Long expiration;
//    private final UserRepository userRepository;
//
//    public JWTUtils(@Value("${jwt.secret}") String secret,
//                    @Value("${jwt.expiration}") Long expiration,
//                    UserRepository userRepository) {
//
//        System.out.println("🚀 JWTUtils constructor called");
//
//        // ✅ SOLUÇÃO: Usar o builder seguro do JJWT
//        this.secretKey = createSecureSecretKey(secret);
//        this.expiration = expiration;
//        this.userRepository = userRepository;
//
//        System.out.println("✅ JWTUtils initialized with secure key");
//    }
//
//    // ✅ MÉTODO PARA CRIAR CHAVE SEGURA
//    private SecretKey createSecureSecretKey(String secretInput) {
//        try {
//            System.out.println("🔄 Creating secure secret key...");
//
//            // Se a string for muito longa, assume que é Base64
//            if (secretInput.length() >= 44) { // Base64 de 32+ bytes
//                try {
//                    byte[] keyBytes = java.util.Base64.getDecoder().decode(secretInput);
//                    if (keyBytes.length >= 32) {
//                        System.out.println("✅ Using Base64 decoded key, length: " + keyBytes.length + " bytes");
//                        return Keys.hmacShaKeyFor(keyBytes);
//                    }
//                } catch (IllegalArgumentException e) {
//                    System.out.println("🔑 Not Base64, using as string");
//                }
//            }
//
//            // ✅ SE NADA FUNCIONAR, GERE UMA CHAVE SEGURA AUTOMATICAMENTE
//            System.out.println("🔑 Generating secure key automatically");
//            return Jwts.SIG.HS512.key().build();
//
//        } catch (Exception e) {
//            System.out.println("❌ Error creating key: " + e.getMessage());
//            // Fallback: gera uma chave segura automaticamente
//            return Jwts.SIG.HS512.key().build();
//        }
//    }
//
//    // MÉTODO PARA DECODIFICAR O SECRET CORRETAMENTE
//    private SecretKey decodeSecretKey(String secret) {
//        try {
//            System.out.println("🔄 Attempting Base64 decode...");
//            byte[] keyBytes = java.util.Base64.getDecoder().decode(secret);
//            System.out.println("✅ Base64 decode successful, key length: " + keyBytes.length);
//            return Keys.hmacShaKeyFor(keyBytes);
//        } catch (IllegalArgumentException e) {
//            System.out.println("🔄 Base64 failed, using as raw string");
//            return Keys.hmacShaKeyFor(secret.getBytes());
//        }
//    }
//
//    public String generateToken(final UserDetailsDTO detailsDTO) {
//        return Jwts.builder()
//                .claim("id", detailsDTO.getId())
//                .claim("name", detailsDTO.getName())
//                .claim("authorities", detailsDTO.getAuthorities())
//                .subject(detailsDTO.getUsername())
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(secretKey, Jwts.SIG.HS512)
//                .compact();
//    }
//
//    public Boolean validateToken(String token) {
//        try {
//            Jwts.parser()
//                    .verifyWith(secretKey)
//                    .build()
//                    .parseSignedClaims(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public String getUsername(String token) {
//        return getClaimsFromToken(token).getSubject();
//    }
//
//    @SuppressWarnings("unchecked")
//    public <T> T getClaimFromToken(String token, String claimName, Class<T> clazz) {
//        Claims claims = getClaimsFromToken(token);
//
//        if (clazz == List.class && "authorities".equals(claimName)) {
//            Object authorities = claims.get(claimName);
//            if (authorities instanceof List) {
//                return (T) authorities;
//            }
//        }
//
//        return claims.get(claimName, clazz);
//    }
//
//    private Claims getClaimsFromToken(String token) {
//        return Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }
//
//    public String generateToken(String username) {
//        UserDetailsDTO userDetails = getUserDetailsByUsername(username);
//        return generateToken(userDetails);
//    }
//
//    private UserDetailsDTO getUserDetailsByUsername(String username) {
//        var user = userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//
//        Collection<? extends GrantedAuthority> authorities = convertRolesToAuthorities(user.getRoles());
//
//        // ✅ USA O FACTORY METHOD (não o construtor)
//        return UserDetailsDTO.create(
//                user.getId().toString(),
//                user.getName(),
//                user.getEmail(),
//                user.getPassword(),
//                authorities
//        );
//    }
//
//    private Collection<? extends GrantedAuthority> convertRolesToAuthorities(Set<UserRole> roles) {
//        return roles.stream()
//                .map(role -> new SimpleGrantedAuthority(role.name()))
//                .collect(Collectors.toList());
//    }
//}
//
////    public Boolean validateToken(String token) {
////        try {
////                Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
////            return true;
////        } catch (Exception e) {
////            return false;
////        }
////    }
////
////    public String getUsername(String token) {
////        return Jwts.parser()
////                .setSigningKey(secret.getBytes())
////                .parseClaimsJws(token)
////                .getBody()
////                .getSubject();
////    }
////
////    public <T> T getClaimFromToken(String token, String claimName, Class<T> clazz) {
////        Claims claims = Jwts.parser()
////                .setSigningKey(secret.getBytes())
////                .parseClaimsJws(token)
////                .getBody();
////
////        return claims.get(claimName, clazz);
////    }
//
////}
