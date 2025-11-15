package com.schoolagenda.application.web.dto.response;

import lombok.Builder;
import lombok.With;

// Responsável pela resposta (payload de saída) de autenticação
@With
@Builder
public record AuthenticationResponse(
        String token,
        String refreshToken,
        String type
) {
}
