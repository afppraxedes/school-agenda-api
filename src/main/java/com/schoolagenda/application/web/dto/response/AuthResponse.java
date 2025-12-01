package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.domain.enums.UserRole;

import java.util.Set;

public class AuthResponse {
    private String token;
    private Long id;
    private String username;
    private String name;
    private Set<UserRole> roles;

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String username, String name, Set<UserRole> roles) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.name = name;
        this.roles = roles;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<UserRole> getRoles() { return roles; }
    public void setRoles(Set<UserRole> roles) { this.roles = roles; }
}