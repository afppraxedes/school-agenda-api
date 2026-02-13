package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.response.RefreshTokenResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.application.web.security.util.JwtService;
import com.schoolagenda.domain.exception.TokenRefreshException;
import com.schoolagenda.domain.model.RefreshToken;
import com.schoolagenda.domain.repository.RefreshTokenRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.application.web.dto.request.TokenRefreshRequest;
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

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            log.warn("❌ Refresh token {} expired and was deleted", token.getToken());
            throw new TokenRefreshException("Refresh token expirado. Por favor, faça login novamente.");
        }
        return token;
    }

    @Transactional
    public RefreshTokenResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        log.info("🔄 Processing refresh token: {}...",
                requestRefreshToken.substring(0, Math.min(10, requestRefreshToken.length())));

        return findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtService.generateToken(
                            //TODO: a solução abaixo é provisória! Refatorar o "UserDetailsDTO.create"!
                            AgendaUserDetails.create(user));
                    log.info("✅ New access token generated for user: {}", user.getEmail());

                    return RefreshTokenResponse.builder()
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
