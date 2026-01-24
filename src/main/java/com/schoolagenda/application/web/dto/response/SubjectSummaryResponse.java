package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.domain.enums.AcademicStatus;

import java.math.BigDecimal;
import java.util.List;
public record SubjectSummaryResponse(
        Long subjectId,
        String subjectName,                  // Nome da matéria
        List<GradeDetailResponse> grades,    // Detalhes das notas
        BigDecimal average,                  // Média atual
        AcademicStatus status,               // Situação acadêmica do aluno na matéria
        BigDecimal pointsNeededToPass,       // Quanto falta para chegar na média 6.0
        boolean canDoRecovery,               // Se o aluno tem direito a prova de recuperação
        BigDecimal attendancePercentage,     // % de presença
        long totalAbsences,                  // Total de faltas
        boolean isApprovedByAttendance,      // Se tem > 75%
        String teacherName                   // Útil para o pai saber com quem falar
) {}
