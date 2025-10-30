package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.AuthResponse;
import com.schoolagenda.application.web.dto.LoginRequest;
import com.schoolagenda.application.web.dto.UserDTO;
import com.schoolagenda.domain.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        if (userService.validateCredentials(loginRequest.getUsername(), loginRequest.getPassword())) {
            return userService.findByUsername(loginRequest.getUsername())
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