//package com.schoolagenda.domain.service;
//
//
//
//import com.schoolagenda.application.web.dto.request.RefreshTokenRequest;
//import com.schoolagenda.application.web.dto.response.RefreshTokenResponse;
//import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
//import com.schoolagenda.application.web.util.JWTUtils;
//import com.schoolagenda.domain.exception.RefreshTokenExpired;
//import com.schoolagenda.domain.exception.ResourceNotFoundException;
//import com.schoolagenda.domain.exception.TokenRefreshException;
//import com.schoolagenda.domain.model.RefreshToken;
//import com.schoolagenda.domain.repository.RefreshTokenRepository;
//import lombok.RequiredArgsConstructor;
//
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class RefreshTokenService {
//
//    @Value("${jwt.expiration-sec.refresh-token}")
//    private Long refreshTokenExpirationSec;
//
//    private final RefreshTokenRepository repository;
//    private final UserDetailsService userDetailsService;
//    private final JWTUtils jwtUtils;
//
//    // Responsável por criar um novo token
//    public RefreshToken save(final String username) {
//        return repository.save(
//                RefreshToken.builder()
//                        .id(UUID.randomUUID().toString())
//                        .createdAt(LocalDateTime.now())
//                        .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationSec))
//                        .username(username)
//                        .build()
//        );
//    }
//
////    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
////        // Invalidar token atual antes de gerar novo
////        var oldRefreshToken = repository.findByToken(request.refreshToken())
////                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
////
////        // Marcar como revogado
////        oldRefreshToken.setRevoked(true);
////        refreshTokenRepository.save(oldRefreshToken);
////
////        // Gerar novo token
////        return generateNewRefreshToken(oldRefreshToken.getUserId());
////    }
//
//    // TODO: Implementação anterior utilizado pelo "FBE"
//    // Responsável pelo response do "refresh token
//    public RefreshTokenResponse refreshToken(final String refreshTokenId) {
//        final var refreshToken = repository.findById(refreshTokenId)
//                .orElseThrow(() -> new TokenRefreshException(("Refresh token not found. ID: " + refreshTokenId)));
//
//         // Se a data de expiração do refresh token e anterior a data atual, será lançada a excessão, pois
//        // o token está expirado.
//        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new RefreshTokenExpired("Refresh token expired. Id: " + refreshTokenId);
//        }
//
//        return new RefreshTokenResponse(
//                jwtUtils.generateToken((UserDetailsDTO) userDetailsService.loadUserByUsername(refreshToken.getUsername()))
//        );
//    }
//}

//package com.schoolagenda.application.service;
package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
import com.schoolagenda.application.web.util.JwtService;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.exception.TokenRefreshException;
import com.schoolagenda.domain.model.RefreshToken;
import com.schoolagenda.domain.repository.RefreshTokenRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.application.web.dto.request.TokenRefreshRequest;
import com.schoolagenda.application.web.dto.response.TokenRefreshResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    @Value("${jwt.refresh-token.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Optional<RefreshToken> findByToken(String token) {
        log.info("🔍 Searching for refresh token in database: {}...",
                token.substring(0, Math.min(10, token.length())));

        Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken(token);

        log.info("🔍 Refresh token found in database: {}", foundToken.isPresent());
        return foundToken;
    }

    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        log.info("💾 Creating refresh token for user ID: {}", userId);

        // Remove tokens antigos do usuário
        userRepository.findById(userId).ifPresent(user -> {
            refreshTokenRepository.deleteByUser(user);
            log.info("🗑️ Removed old refresh tokens for user: {}", user.getEmail());
        });

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId)));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);

        log.info("✅ Refresh token created and saved: {} for user: {}",
                savedToken.getToken(), savedToken.getUser().getEmail());

        return savedToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        log.info("🔄 Processing refresh token: {}...",
                requestRefreshToken.substring(0, Math.min(10, requestRefreshToken.length())));

        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(
                            //TODO: a solução abaixo é provisória! Refatorar o "UserDetailsDTO.create"!
                            UserDetailsDTO.create(user));
                    log.info("✅ New access token generated for user: {}", user.getEmail());

                    return TokenRefreshResponse.builder()
                            .accessToken(token)
                            .refreshToken(requestRefreshToken) // Mantém o mesmo refresh token
                            .build();
                })
                .orElseThrow(() -> {
                    log.error("❌ Refresh token not found in database: {}", requestRefreshToken);
                    return new TokenRefreshException("Refresh token is not in database!");
                });
    }
}

// TODO: FUNCIONANDO CORRETAMENTE, MAS SEM SALVAR O "REFRESH TOKEN"
//@Service
//@RequiredArgsConstructor
//public class RefreshTokenService {
//
//    @Value("${jwt.refresh-token.expiration}")
//    private Long refreshTokenDurationMs;
//
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final UserRepository userRepository;
//    private final JwtService jwtService;
//
//    public Optional<RefreshToken> findByToken(String token) {
//        return refreshTokenRepository.findByToken(token);
//    }
//
//    public RefreshToken createRefreshToken(Long userId) {
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setUser(userRepository.findById(userId).get());
//        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
//        refreshToken.setToken(UUID.randomUUID().toString());
//
//        refreshToken = refreshTokenRepository.save(refreshToken);
//        return refreshToken;
//    }
//
//    public RefreshToken verifyExpiration(RefreshToken token) {
//        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
//            refreshTokenRepository.delete(token);
//            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
//        }
//        return token;
//    }
//
//    @Transactional
//    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
//        String requestRefreshToken = request.getRefreshToken();
//
//        return findByToken(requestRefreshToken)
//                .map(this::verifyExpiration)
//                .map(RefreshToken::getUser)
//                .map(user -> {
//                    String token = jwtService.generateToken(user);
//                    return TokenRefreshResponse.builder()
//                            .accessToken(token)
//                            .refreshToken(requestRefreshToken)
//                            .build();
//                })
//                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
//    }
//}
