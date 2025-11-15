package com.schoolagenda.application.web.security;

import com.schoolagenda.application.web.dto.request.AuthenticateRequest;
import com.schoolagenda.application.web.dto.response.AuthenticationResponse;
import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
import com.schoolagenda.application.web.util.JWTUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

// Nosso Filtro de Autenticação
@Log4j2
@RequiredArgsConstructor
public class JWTAuthenticationImpl {

    private final JWTUtils jwtUtils;

    // Gerenciador de autenticação
    private final AuthenticationManager authenticationManager;

    // Retornando o "payload de resposta" com as informações do token
    public AuthenticationResponse authenticate(final AuthenticateRequest request) {
        log.info("Authenticating user: {}", request.email());
        try {
            final var authResult = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            // retornando usuário logado
            return buildAuthenticationResponse((UserDetailsDTO) authResult.getPrincipal());
        } catch (BadCredentialsException ex) {
            log.error("Error on authenticate user: {}", request.email());
            throw new BadCredentialsException("Email or password invalid");
        }
    }

    // Responsável por criar/construir o response de autenticação
    protected AuthenticationResponse buildAuthenticationResponse(final UserDetailsDTO detailsDTO) {
        // log.info("Successfully authenticated user: {}", detailsDTO.getUsername());
        final var token = jwtUtils.generateToken(detailsDTO);
        return AuthenticationResponse.builder()
                .type("Bearer")
                .token(token)
                .build();
    }

}
