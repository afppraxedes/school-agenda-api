package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.enums.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record SchoolEventRequest(
        @NotBlank String title,
        String description,
        @NotNull OffsetDateTime startDate,
        @NotNull OffsetDateTime endDate,
        boolean allDay,
        @NotNull EventType type,
        Long schoolClassId, // Nulo se for para a escola toda
        String location
) {}
