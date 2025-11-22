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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String type;        // "Bearer"
    private String token;       // JWT Access Token
    private String refreshToken;
}
