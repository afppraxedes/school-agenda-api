package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.domain.enums.AcademicStatus;

import java.math.BigDecimal;
import java.util.List;

public record SubjectSummaryResponse(
        Long subjectId,
        String subjectName,
        List<GradeDetailResponse> grades,
        BigDecimal average,
//        boolean isApproved // Ex: average >= 6.0
        AcademicStatus status,      // Novo campo
        BigDecimal pointsNeeded,    // Quanto falta para atingir a média (ex: 6.0)
        boolean canDoRecovery,      // Se o aluno tem direito a prova de recuperação
        BigDecimal attendancePercentage, // Novo: % de presença
        long totalAbsences,              // Novo: Total de faltas
        boolean isApprovedByAttendance   // Novo: Se tem > 75%
) {}
