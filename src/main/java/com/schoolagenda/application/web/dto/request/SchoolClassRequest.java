package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SchoolClassRequest(
        @NotBlank @Size(max = 100)
        String name,

        @Size(max = 500)
        String description,

        @NotNull
        Integer year,

        @NotNull
        Boolean isActive,

        @NotNull
        User coordinator
) {}