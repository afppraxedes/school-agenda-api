package com.schoolagenda.application.web.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record ReportCardResponse(
        Long studentId,
        String studentName,
        String schoolClassName,
        List<SubjectSummaryResponse> subjects,
        BigDecimal globalAverage
) {}
