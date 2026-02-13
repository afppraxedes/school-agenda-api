package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.response.RefreshTokenResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.application.web.security.util.JwtService;
import com.schoolagenda.domain.exception.TokenRefreshException;
import com.schoolagenda.domain.model.RefreshToken;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.application.web.dto.request.AuthenticationRequest;
import com.schoolagenda.application.web.dto.response.AuthenticationResponse;
import com.schoolagenda.application.web.dto.request.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        var user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .roles(Set.of(UserRole.DIRECTOR))
                .build();

        var savedUser = userRepository.save(user);

        // ✅ CORREÇÃO: Salva o refresh token no banco
        var refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

        var accessToken = jwtService.generateToken(AgendaUserDetails.create(savedUser));

        return AuthenticationResponse.builder()
                .type("Bearer")
                // TODO: refatorar o atributo "token" para "accessToken". Após, terá que ser alterado
                // tamém na aba "Tests" no Postman para a obtenção do "Token"!
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken()) // ✅ Usa o token salvo no banco
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            var user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            var accessToken = jwtService.generateToken(AgendaUserDetails.create(user));

            // ✅ CORREÇÃO: Salva o refresh token no banco
            var refreshToken = refreshTokenService.createRefreshToken(user.getId());

            log.info("✅ Login successful for user: {}", user.getEmail());
            log.info("✅ Refresh token saved to database: {}", refreshToken.getToken());

            return AuthenticationResponse.builder()
                    .type("Bearer")
                    // TODO: refatorar o atributo "token" para "accessToken". Após, terá que ser alterado
                    // tamém na aba "Tests" no Postman para a obtenção do "Token"!
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken()) // ✅ Usa o token salvo no banco
                    .build();

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    @Transactional
    public RefreshTokenResponse processRefreshToken(String requestRefreshToken) {
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Gera o novo Access Token baseado no usuário dono do Refresh Token
                    String newAccessToken = jwtService.generateToken(AgendaUserDetails.create(user));

                    log.info("🔄 Access Token renovado com sucesso para: {}", user.getEmail());

                    return RefreshTokenResponse.builder()
                            .accessToken(newAccessToken)
                            .refreshToken(requestRefreshToken) // Mantemos a rotação simples (reutiliza o UUID)
                            .build();
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token não encontrado no sistema."));
    }
}