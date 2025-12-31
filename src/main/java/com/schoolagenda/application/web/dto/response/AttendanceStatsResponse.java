package com.schoolagenda.application.web.dto.response;

public record AttendanceStatsResponse(
        long totalAbsences,
        long totalClasses,
        double attendancePercentage
) {
    // Construtor compacto para calcular a porcentagem automaticamente
    public AttendanceStatsResponse(long totalAbsences, long totalClasses) {
        this(totalAbsences, totalClasses,
                totalClasses > 0 ? ((double)(totalClasses - totalAbsences) / totalClasses) * 100 : 100.0);
    }
}
