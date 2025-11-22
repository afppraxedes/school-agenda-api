package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.AuthenticationRequest;
import com.schoolagenda.application.web.dto.response.AuthenticationResponse;
import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
import com.schoolagenda.application.web.util.JWTUtils;
import com.schoolagenda.application.web.util.JwtService;
import com.schoolagenda.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService; // ✅ Adicione esta dependência

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

            // ✅ CORREÇÃO: Carrega UserDetails do Spring Security
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            var accessToken = jwtService.generateToken(userDetails);
            var refreshToken = jwtService.generateRefreshToken(userDetails);

            return AuthenticationResponse.builder()
                    .type("Bearer")
                    .token(accessToken)
//                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

//    private AuthenticationResponse buildAuthenticationResponse(final UserDetailsDTO detailsDTO) {
//        log.info("Successfully authenticated user: {}", detailsDTO.getUsername());
//        final var token = jwtUtils.generateToken(detailsDTO);
//        return AuthenticationResponse.builder()
//                .type("Bearer")
//                .token(token)
//                .build();
//    }
}