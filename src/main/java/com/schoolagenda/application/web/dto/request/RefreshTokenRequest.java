package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Request do "RefreshToken"
public record RefreshTokenRequest(
        @Size(min = 16, max = 50, message = "Refresh token must be between 16 and 30 characters")
        @NotBlank(message = "Refresh token is required")
        String refreshToken
) {
}
