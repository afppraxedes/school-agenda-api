package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record StudentAttendanceRequest(
        @NotNull Long studentId,
        @NotNull boolean present,
        String note
) {}
