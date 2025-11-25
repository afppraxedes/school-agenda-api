package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.AuthenticationRequest;
import com.schoolagenda.application.web.dto.response.AuthenticationResponse;
import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
import com.schoolagenda.application.web.util.JwtService;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private final JwtService jwtService; // ✅ Use apenas JwtService
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            // 1. Autenticação
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // 2. Buscar usuário para obter dados completos
            String username = authentication.getName();
            var user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // 3. Converter para UserDetailsDTO
            UserDetailsDTO userDetails = convertToUserDetailsDTO(user);

            // 4. ✅ USAR JwtService PARA GERAR TOKEN
            String accessToken = jwtService.generateToken(userDetails);

            log.info("✅ Login successful for user: {}", user.getEmail());

            return AuthenticationResponse.builder()
                    .type("Bearer")
                    .token(accessToken)
                    .refreshToken("")
                    .build();

        } catch (BadCredentialsException e) {
            log.error("❌ Authentication failed for user: {}", request.getEmail());
            throw new RuntimeException("Invalid email or password");
        }
    }

    private UserDetailsDTO convertToUserDetailsDTO(User user) {
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());

        return new UserDetailsDTO(
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}

//@Service
//@RequiredArgsConstructor
//@Log4j2
//public class AuthService {
//
//    private final JWTUtils jwtUtils;
//    private final AuthenticationManager authenticationManager;
//    private final UserRepository userRepository; // ✅ Adicione se precisar
//
////    public AuthenticationResponse authenticate(AuthenticationRequest request) {
////        try {
////            // 1. Autenticação
////            Authentication authentication = authenticationManager.authenticate(
////                    new UsernamePasswordAuthenticationToken(
////                            request.getEmail(),
////                            request.getPassword()
////                    )
////            );
////
////            // 2. ✅ CORREÇÃO: Obter o username do principal
////            String username = authentication.getName();
////
////            // 3. ✅ Buscar o usuário e converter para UserDetailsDTO
////            var user = userRepository.findByEmail(username)
////                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
////
////            // 4. ✅ Converter User para UserDetailsDTO
////            UserDetailsDTO userDetails = convertToUserDetailsDTO(user);
////
////            // 5. Gerar token
////            String accessToken = jwtUtils.generateToken(userDetails);
////
////            return AuthenticationResponse.builder()
////                    .type("Bearer")
////                    .token(accessToken)
////                    .refreshToken("")
////                    .build();
////
////        } catch (BadCredentialsException e) {
////            throw new RuntimeException("Invalid email or password");
////        }
////    }
//
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getEmail(),
//                            request.getPassword()
//                    )
//            );
//
//            // ✅ SOLUÇÃO UNIVERSAL: Sempre buscar do repositório
//            String username = authentication.getName(); // Isso será o email
//
//            var user = userRepository.findByEmail(username)
//                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
//
//            UserDetailsDTO userDetails = convertToUserDetailsDTO(user);
//            String accessToken = jwtUtils.generateToken(userDetails);
//
//            // Debug
//            System.out.println("✅ AUTHENTICATION SUCCESSFUL:");
//            System.out.println("   User: " + userDetails.getEmail());
//            System.out.println("   ID: " + userDetails.getId());
//            System.out.println("   Roles: " + userDetails.getAuthorities());
//
//            return AuthenticationResponse.builder()
//                    .type("Bearer")
//                    .token(accessToken)
//                    .refreshToken("")
//                    .build();
//
//        } catch (BadCredentialsException e) {
//            throw new RuntimeException("Invalid email or password");
//        }
//    }
//
//    // ✅ MÉTODO PARA CONVERTER USER PARA USERDETAILSDTO
//    private UserDetailsDTO convertToUserDetailsDTO(User user) {
//        Collection<GrantedAuthority> authorities = user.getRoles().stream()
//                .map(role -> new SimpleGrantedAuthority(role.name())) // ou role.getDescription()
//                .collect(Collectors.toList());
//
//        return new UserDetailsDTO(
//                user.getId().toString(),
//                user.getName(),
//                user.getEmail(),
//                user.getPassword(),
//                authorities
//        );
//    }
//}

//@Service
//@RequiredArgsConstructor
//@Log4j2
//public class AuthService {
//
//    private final JWTUtils jwtUtils;       // ✅ USE ESTE
//    private final AuthenticationManager authenticationManager;
//
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getEmail(),
//                            request.getPassword()
//                    )
//            );
//
//            UserDetailsDTO userDetails = (UserDetailsDTO) authentication.getPrincipal();
//
//            // ✅ GARANTINDO QUE USA JWTUtils
//            String accessToken = jwtUtils.generateToken(userDetails);
//
//            // ✅ DEBUG FINAL
//            System.out.println("🎯 TOKEN FINAL:");
//            jwtUtils.debugToken(accessToken);
//
//            return AuthenticationResponse.builder()
//                    .type("Bearer")
//                    .token(accessToken)
//                    .refreshToken("")
//                    .build();
//
//        } catch (BadCredentialsException e) {
//            throw new RuntimeException("Invalid email or password");
//        }
//    }

//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        try {
//            // 1. Autenticação
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getEmail(),
//                            request.getPassword()
//                    )
//            );
//
//            // 2. Obter UserDetails
//            UserDetailsDTO userDetails = (UserDetailsDTO) authentication.getPrincipal();
//
//            // 3. Gerar token (agora com payload completo)
//            String accessToken = jwtUtils.generateToken(userDetails);
//            // var refreshToken = jwtUtils.generateRefreshToken(userDetails); // Implementar se necessário
//
//            // ✅ DEBUG: IMPRIMIR PAYLOAD NO CONSOLE
//            jwtUtils.printTokenPayload(accessToken);
//
//            // 4. Retornar response (sem as informações no response body)
//            return AuthenticationResponse.builder()
//                    .type("Bearer")
//                    .token(accessToken)
//                    .refreshToken("") // ou null se não usar refresh token
//                    .build();
//
//        } catch (BadCredentialsException e) {
//            throw new RuntimeException("Invalid email or password");
//        }
//    }
//}

//@Service
//@RequiredArgsConstructor
//@Log4j2
//public class AuthService {
//
//    private final JWTUtils jwtUtils;
//    private final AuthenticationManager authenticationManager;
//
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        try {
//            log.info("Attempting authentication for user: {}", request.getEmail());
//
//            // 1. Autenticação pelo Spring Security
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getEmail(),
//                            request.getPassword()
//                    )
//            );
//
//            // 2. Obter UserDetails do resultado da autenticação
//            UserDetailsDTO userDetails = (UserDetailsDTO) authentication.getPrincipal();
//
//            // 3. Gerar token
//            String accessToken = jwtUtils.generateToken(userDetails);
//
//            // 4. Log de sucesso
//            log.info("Successfully authenticated user: {} (ID: {})",
//                    userDetails.getEmail(), userDetails.getId());
//
//            // 5. Retornar response
//            return AuthenticationResponse.of(
//                    accessToken,
//                    null, // ou implementar refresh token depois
//                    jwtUtils.getExpirationTime() / 1000, // converter para segundos
//                    userDetails
//            );
//
//        } catch (BadCredentialsException e) {
//            log.warn("Failed authentication attempt for user: {}", request.getEmail());
//            throw new RuntimeException("Invalid email or password");
//        } catch (Exception e) {
//            log.error("Unexpected error during authentication for user: {}",
//                    request.getEmail(), e);
//            throw new RuntimeException("Authentication failed");
//        }
//    }
//}

//@Service
//@RequiredArgsConstructor
//@Log4j2
//public class AuthService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JWTUtils jwtUtils; // ✅ Usar apenas JWTUtils
//    private final AuthenticationManager authenticationManager;
//
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        try {
//            // 1. Autenticar com Spring Security
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getEmail(),
//                            request.getPassword()
//                    )
//            );
//
//            // 2. Extrair UserDetails da autenticação (SEM buscar novamente no banco)
//            UserDetailsDTO userDetails = (UserDetailsDTO) authentication.getPrincipal();
//
//            // 3. Gerar tokens usando JWTUtils
//            var accessToken = jwtUtils.generateToken(userDetails);
//            var refreshToken = jwtUtils.generateRefreshToken(userDetails); // Implementar se necessário
//
//            log.info("Successfully authenticated user: {}", userDetails.getUsername());
//
//            return AuthenticationResponse.builder()
//                    .type("Bearer")
//                    .token(accessToken)
//                    .refreshToken(refreshToken)
//                    .expiresIn(jwtUtils.getExpirationTime()) // ✅ Adicionar tempo de expiração
//                    .build();
//
//        } catch (BadCredentialsException e) {
//            log.error("Authentication failed for user: {}", request.getEmail());
//            throw new RuntimeException("Invalid email or password");
//        }
//    }

//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;
//    private final UserDetailsService userDetailsService; // ✅ Adicione esta dependência
//
//    public AuthenticationResponse authenticate(AuthenticationRequest request) {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            request.getEmail(),
//                            request.getPassword()
//                    )
//            );
//
//            var user = userRepository.findByEmail(request.getEmail())
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//            // ✅ CORREÇÃO: Carrega UserDetails do Spring Security
//            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
//
//            var accessToken = jwtService.generateToken(userDetails);
//            var refreshToken = jwtService.generateRefreshToken(userDetails);
//
//            return AuthenticationResponse.builder()
//                    .type("Bearer")
//                    .token(accessToken)
////                    .accessToken(accessToken)
//                    .refreshToken(refreshToken)
//                    .build();
//
//        } catch (BadCredentialsException e) {
//            throw new RuntimeException("Invalid email or password");
//        }
//    }

//    private AuthenticationResponse buildAuthenticationResponse(final UserDetailsDTO detailsDTO) {
//        log.info("Successfully authenticated user: {}", detailsDTO.getUsername());
//        final var token = jwtUtils.generateToken(detailsDTO);
//        return AuthenticationResponse.builder()
//                .type("Bearer")
//                .token(token)
//                .build();
//    }
//}