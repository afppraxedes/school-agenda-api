package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public class UpdateUserRequest {

    // NOTA: Para a atualização de um usuário, não podemos utilizar as validações
    // de "@NotBlank", "@NotNull" etc., pois podemos apenas querer atualizar um
    // campo. Mas como os outros estariam com validações, daria problemas ao atualizar!
    // Para resolver este problema, é que foi criada esta classe com a finalidade de
    // "atualizar" os dados do usuário, tanto de forma total como parcial!

    @Schema(description = "User name", example = "Alexander Praxedes")
    @Size(min = 3, max = 50, message = "Name must contain between 3 and 50 characters")
    private String name;

    @Schema(description = "User username", example = "Alexander Praxedes")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "User email", example = "alex@mail.com")
    @Email(message = "Invalid email")
    @Size(min = 6, max = 50, message = "Email must contain between 6 and 50 characters")
    private String email;

    @Schema(description = "User password", example = "123456")
    @Size(min = 6, max = 50, message = "Password must contain between 6 and 50 characters")
    private String password;

    // TODO: verificar se altero para "profiles" e coloco os perfis iniciando com "ROLE"!
    @Schema(description = "User profiles", example = "[\"ADMINISTRATOR\", \"DIRECTOR\", \"TEACHER\", \"RESPONSIBLE\", \"STUDENT\"]")
    private Set<UserRole> roles;

    @Schema(description = "User push subscription", example = "... (colocar o exemplo")
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