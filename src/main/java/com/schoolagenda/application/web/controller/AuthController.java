//package com.schoolagenda.application.web.controller.impl;
//
//import com.schoolagenda.application.web.controller.AuthController;
//import com.schoolagenda.application.web.dto.request.AuthenticateRequest;
//import com.schoolagenda.application.web.dto.request.RefreshTokenRequest;
//import com.schoolagenda.application.web.dto.response.AuthenticationResponse;
//import com.schoolagenda.application.web.dto.response.RefreshTokenResponse;
//import com.schoolagenda.application.web.security.JWTAuthenticationImpl;
//import com.schoolagenda.application.web.util.JWTUtils;
//import com.schoolagenda.domain.service.RefreshTokenService;
//import lombok.RequiredArgsConstructor;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class AuthControllerImpl implements AuthController {
//
//    private final JWTUtils jwtUtils;
//    private final RefreshTokenService refreshTokenService;
//    private final AuthenticationConfiguration authenticationConfiguration;
//
//    public AuthControllerImpl(JWTUtils jwtUtils, RefreshTokenService refreshTokenService, AuthenticationConfiguration authenticationConfiguration) {
//        this.jwtUtils = jwtUtils;
//        this.refreshTokenService = refreshTokenService;
//        this.authenticationConfiguration = authenticationConfiguration;
//    }
//
//    // Responsável pela autenticação (criação do token)
//    @Override
//    public ResponseEntity<AuthenticationResponse> authenticate(final AuthenticateRequest request) throws Exception {
//
//        System.out.println("Email: " + request.email() + "\nPassword: " + request.password());
//
//        return ResponseEntity.ok().body(
//                new JWTAuthenticationImpl(jwtUtils, authenticationConfiguration.getAuthenticationManager())
//                        .authenticate(request)
//                        .withRefreshToken(refreshTokenService.save(request.email()).getId())
//        );
//    }
//
//    // Responsável pelo "refresh token"
//    @Override
//    public ResponseEntity<RefreshTokenResponse> refreshToken(RefreshTokenRequest request) {
//        return ResponseEntity.ok().body(refreshTokenService.refreshToken(request.refreshToken()));
//    }
//}

package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.AuthenticationRequest;
import com.schoolagenda.application.web.dto.request.RegisterRequest;
import com.schoolagenda.application.web.dto.request.TokenRefreshRequest;
import com.schoolagenda.application.web.dto.response.AuthenticationResponse;
import com.schoolagenda.application.web.dto.response.TokenRefreshResponse;
import com.schoolagenda.domain.service.AuthenticationService;
import com.schoolagenda.domain.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(refreshTokenService.refreshToken(request));
    }
}

