package com.schoolagenda.domain.service;



import com.schoolagenda.application.web.dto.response.RefreshTokenResponse;
import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
import com.schoolagenda.application.web.util.JWTUtils;
import com.schoolagenda.domain.exception.RefreshTokenExpired;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.RefreshToken;
import com.schoolagenda.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.expiration-sec.refresh-token}")
    private Long refreshTokenExpirationSec;

    private final RefreshTokenRepository repository;
    private final UserDetailsService userDetailsService;
    private final JWTUtils jwtUtils;

    // Responsável por criar um novo token
    public RefreshToken save(final String username) {
        return repository.save(
                RefreshToken.builder()
                        .id(UUID.randomUUID().toString())
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationSec))
                        .username(username)
                        .build()
        );
    }

    // Responsável pelo response do "refresh token
    public RefreshTokenResponse refreshToken(final String refreshTokenId) {
        final var refreshToken = repository.findById(refreshTokenId)
                .orElseThrow(() -> new ResourceNotFoundException(("Refresh token not found. ID: " + refreshTokenId)));

         // Se a data de expiração do refresh token e anterior a data atual, será lançada a excessão, pois
        // o token está expirado.
        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenExpired("Refresh token expired. Id: " + refreshTokenId);
        }

        return new RefreshTokenResponse(
                jwtUtils.generateToken((UserDetailsDTO) userDetailsService.loadUserByUsername(refreshToken.getUsername()))
        );
    }
}
