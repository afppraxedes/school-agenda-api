//package com.schoolagenda.application.web.dto.request;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//
//import java.io.Serial;
//import java.io.Serializable;
//
//// Responsável pela requisição (payload de entrada) de autenticação
//public record AuthenticateRequest(
//        @Schema(description = "User email", example = "alex@mail.com")
//        @Email(message = "Invalid email")
//        @NotBlank(message = "Email cannot be empty")
//        @Size(min = 6, max = 50, message = "Email must contain between 6 and 50 characters")
//        String email,
//
//        @Schema(description = "User password", example = "123456")
//        @NotBlank(message = "Password cannot be empty")
//        @Size(min = 6, max = 50, message = "Password must contain between 6 and 50 characters")
//        String password
//) implements Serializable {
//    @Serial
//    private static final long serialVersionUID = 1L;
//}

package com.schoolagenda.application.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
    private String email;
    private String password;
}
