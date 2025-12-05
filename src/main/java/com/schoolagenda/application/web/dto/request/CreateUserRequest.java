package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

//@With
//public record CreateUserRequest(
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest{
        @Schema(description = "User name", example = "Alexander Praxedes")
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 3, max = 50, message = "Name must contain between 3 and 50 characters")
        private String name;

        @Schema(description = "User username", example = "Alexander Praxedes")
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;

        @Schema(description = "User email", example = "alex@mail.com")
        @Email(message = "Invalid email")
        @NotBlank(message = "Email cannot be empty")
        @Size(min = 6, max = 50, message = "Email must contain between 6 and 50 characters")
        private String email;

        @Schema(description = "User password", example = "123456")
        @NotBlank(message = "Password cannot be empty")
        @Size(min = 6, max = 50, message = "Password must contain between 6 and 50 characters")
        private String password;

        // TODO: verificar se altero para "profiles" e coloco os perfis iniciando com "ROLE"!
        @Schema(description = "User profiles", example = "[\"ADMINISTRATOR\", \"DIRECTOR\", \"TEACHER\", \"RESPONSIBLE\", \"STUDENT\"]")
        @NotNull(message = "Roles are required")
        private Set<UserRole> roles;

        @Schema(description = "User push subscription", example = "... (colocar o exemplo")
        private String pushSubscription;
}
