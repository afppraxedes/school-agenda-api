package com.schoolagenda.application.web.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record StudentDashboardResponse(
        // Resumo Acadêmico
//        BigDecimal globalAverage,
//        BigDecimal globalAttendance,
//
//        // Próximos Eventos (Provas, Feriados)
//        List<SchoolEventResponse> upcomingEvents,
//
//        // Comunicação
//        long unreadMessagesCount,
//
//        // Status do Dia
//        List<TimetableResponse> todaysSchedule,
//        String dailyMessage, // Uma frase motivacional ou aviso urgente
//
//        List<MonthlyAverageDTO> performanceHistory

        //Long classId,
        Long teacherClassId,
        BigDecimal globalAverage,
        BigDecimal globalAttendance,
        List<SchoolEventResponse> upcomingEvents, // Já existente no seu projeto
        long unreadMessagesCount,
        List<TimetableResponse> todaysSchedule, // Já existente no seu projeto
        String dailyMessage,
        List<MonthlyAverageDTO> performanceHistory // Adicionado para o Chart.js
) {}
