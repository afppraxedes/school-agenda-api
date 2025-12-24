//package com.schoolagenda.application.service;
package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.application.web.security.util.JwtService;
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
//    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService; // ✅ Adicione esta dependência

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
                .token(accessToken)
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
                    .token(accessToken)
                    .refreshToken(refreshToken.getToken()) // ✅ Usa o token salvo no banco
                    .build();

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }
}

      // TODO: FLUXO ABAIXO CORRETO, MAS NÃO SALVANDO O "REFRESH TOKEN"
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final JwtService jwtService;
//    private final AuthenticationManager authenticationManager;

//    @Transactional
//    public AuthenticationResponse register(RegisterRequest request) {
//        // Verifica se usuário já existe
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("User already exists with email: " + request.getEmail());
//        }
//
//        var user = User.builder()
//                .name(request.getName())
//                .email(request.getEmail())
//                .username(request.getEmail()) // Usa email como username
//                .password(passwordEncoder.encode(request.getPassword()))
//                .roles(Set.of(UserRole.DIRECTOR)) // Default role
//                .build();
//
//        var savedUser = userRepository.save(user);
//
//        // ✅ AGORA: Pode passar User diretamente para JwtService
//        var accessToken = jwtService.generateToken(savedUser);
//        var refreshToken = jwtService.generateRefreshToken(savedUser);
//
//        return AuthenticationResponse.builder()
//                .type("Bearer")
//                .token(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }
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
//            // ✅ AGORA: Pode passar User diretamente
//            var accessToken = jwtService.generateToken(user);
//            var refreshToken = jwtService.generateRefreshToken(user);
//
//            return AuthenticationResponse.builder()
//                    .type("Bearer")
//                    .token(accessToken)
//                    .refreshToken(refreshToken)
//                    .build();
//
//        } catch (BadCredentialsException e) {
//            throw new RuntimeException("Invalid email or password");
//        }
//    }
//}