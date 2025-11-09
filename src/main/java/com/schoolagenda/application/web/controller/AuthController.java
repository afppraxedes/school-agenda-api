package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.response.AuthResponse;
import com.schoolagenda.application.web.dto.request.LoginRequest;
import com.schoolagenda.domain.service.impl.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final UserServiceImpl userServiceImpl;

    public AuthController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        if (userServiceImpl.validateCredentials(loginRequest.getUsername(), loginRequest.getPassword())) {
            return userServiceImpl.findByUsername(loginRequest.getUsername())
                    .map(user -> {
                        // Create basic auth token
                        String credentials = loginRequest.getUsername() + ":" + loginRequest.getPassword();
                        String token = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

                        AuthResponse response = new AuthResponse();
                        response.setToken(token);
                        response.setId(user.getId());
                        response.setUsername(user.getUsername());
                        response.setName(user.getName());
                        response.setRoles(user.getRoles());

                        return ResponseEntity.ok(response);
                    })
                    .orElse(ResponseEntity.status(401).build());
        }

        return ResponseEntity.status(401).build();
    }
}