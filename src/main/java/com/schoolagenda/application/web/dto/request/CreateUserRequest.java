package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.model.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class CreateUserRequest {

    @Schema(description = "User email", example = "alex@mail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Schema(description = "User username", example = "Alexander Praxedes")
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "User password", example = "123456")
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Schema(description = "User username", example = "Alexander Praxedes")
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    // TODO: verificar se altero para "profiles" e coloco os perfis iniciando com "ROLE"!
    @Schema(description = "User profiles", example = "[\"DIRECTOR\", \"TEACHER\", \"RESPONSIBLE\"]")
    @NotNull(message = "Roles are required")
    private Set<UserRole> roles;

    private String pushSubscription;

    // Constructors
    public CreateUserRequest() {}

    public CreateUserRequest(String email, String username, String password, String name,
                             Set<UserRole> roles, String pushSubscription) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.name = name;
        this.roles = roles;
        this.pushSubscription = pushSubscription;
    }

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