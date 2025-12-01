package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class UpdateUserRequest {

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    private Set<UserRole> roles;

    private String pushSubscription;

    // Constructors
    public UpdateUserRequest() {}

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<UserRole> getRoles() { return roles; }
    public void setRoles(Set<UserRole> roles) { this.roles = roles; }

    public String getPushSubscription() { return pushSubscription; }
    public void setPushSubscription(String pushSubscription) { this.pushSubscription = pushSubscription; }
}