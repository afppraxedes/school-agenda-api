package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.domain.enums.AcademicStatus;

import java.math.BigDecimal;
import java.util.List;

public record ReportCardResponse(
        Long studentId,
        String studentName,
        String schoolClassName,
        List<SubjectSummaryResponse> subjects,
        BigDecimal globalAverage,
        AcademicStatus globalStatus // Ex: APROVADO, EM_CURSO
) {}
