package com.schoolagenda.application.web.dto.response;

import java.math.BigDecimal;

public record GradeDetailResponse(
        String assessmentTitle,
        BigDecimal score,
        BigDecimal weight
) {}