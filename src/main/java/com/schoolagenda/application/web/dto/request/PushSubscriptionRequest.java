package com.schoolagenda.application.web.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PushSubscriptionRequest(
        @NotBlank(message = "O endpoint é obrigatório")
        String endpoint,

        Long expirationTime, // opcional, pode ser null

        @NotNull(message = "As chaves de criptografia são obrigatórias")
        @Valid
        KeysDTO keys
) {
    public record KeysDTO(
            @NotBlank(message = "A chave p256dh é obrigatória")
            String p256dh,

            @NotBlank(message = "O auth secret é obrigatório")
            String auth
    ) {}
}