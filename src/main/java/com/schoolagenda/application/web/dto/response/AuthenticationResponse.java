//package com.schoolagenda.application.web.dto.response;
//
//import lombok.Builder;
//import lombok.With;
//
//// Responsável pela resposta (payload de saída) de autenticação
//@With
//@Builder
//public record AuthenticationResponse(
//        String token,
//        String refreshToken,
//        String type
//) {
//}

package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import lombok.Builder;

//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class AuthenticationResponse {
//    private String type;        // "Bearer"
//    private String token;       // JWT Access Token
//    private String refreshToken;
//}

// 📁 dto/response/AuthenticationResponse.java
@Builder
public record AuthenticationResponse(
        String type,        // "Bearer"
        String accessToken,       // access token JWT
        String refreshToken // refresh token (opcional)
) {
    // Método estático para construção
    public static AuthenticationResponse of(String token, String refreshToken,
                                            Long expiresIn, AgendaUserDetails userDetails) {
        return AuthenticationResponse.builder()
                .type("Bearer")
                .accessToken(token)
                .refreshToken(refreshToken)
//                .expiresIn(expiresIn)
//                .issuedAt(Instant.now())
//                .userId(userDetails.getId())
//                .userName(userDetails.getName())
                .build();
    }
}
