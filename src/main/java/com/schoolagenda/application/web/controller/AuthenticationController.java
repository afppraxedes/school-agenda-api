//package com.schoolagenda.application.web.controller;
package com.schoolagenda.application.web.controller;


import com.schoolagenda.application.web.dto.request.RegisterRequest;
import com.schoolagenda.application.web.dto.request.AuthenticationRequest;
//import com.schoolagenda.application.web.dto.request.RegisterRequest;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

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
