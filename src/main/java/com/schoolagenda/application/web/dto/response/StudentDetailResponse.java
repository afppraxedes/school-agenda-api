package com.schoolagenda.application.web.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record StudentDetailResponse(
        Long id,
        String name,
        String className,
        BigDecimal globalAverage,
        BigDecimal attendance,
        String avatarUrl,
        String status // Ex: 'ATIVO', 'FERIAS'
) {}