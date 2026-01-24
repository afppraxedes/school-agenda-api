package com.schoolagenda.application.web.dto.common.attendance;

public record AttendanceSummary(
        Long subjectId,
        long totalClasses,
        long totalAbsences
) {
    // Método utilitário para o caso de uma disciplina não ter registros ainda
    public static AttendanceSummary empty(Long subjectId) {
        return new AttendanceSummary(subjectId, 0L, 0L);
    }
}
