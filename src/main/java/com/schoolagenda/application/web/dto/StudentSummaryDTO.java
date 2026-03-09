package com.schoolagenda.application.web.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record StudentSummaryDTO(
        Long studentId,
        String name,
        String className,
        BigDecimal globalAverage, // O valor 8.17 aparecerá aqui também
        BigDecimal attendance
) {}
