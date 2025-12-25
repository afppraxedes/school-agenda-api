package com.schoolagenda.application.web.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SubjectSummaryResponse(
        Long subjectId,
        String subjectName,
        List<GradeDetailResponse> grades,
        BigDecimal average,
        boolean isApproved // Ex: average >= 6.0
) {}
