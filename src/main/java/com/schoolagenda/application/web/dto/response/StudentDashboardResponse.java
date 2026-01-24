package com.schoolagenda.application.web.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record StudentDashboardResponse(
        // Resumo Acadêmico
        BigDecimal globalAverage,
        BigDecimal globalAttendance,

        // Próximos Eventos (Provas, Feriados)
        List<SchoolEventResponse> upcomingEvents,

        // Comunicação
        long unreadMessagesCount,

        // Status do Dia
        List<TimetableResponse> todaysSchedule,
        String dailyMessage // Uma frase motivacional ou aviso urgente
) {}
