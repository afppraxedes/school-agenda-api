package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.response.StudentDashboardResponse;
import com.schoolagenda.application.web.dto.response.SubjectSummaryResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final GradeService gradeService;
    private final SchoolEventService eventService;
    private final MessageService messageService;
    private final TimetableService timetableService;

    @Transactional(readOnly = true)
    public StudentDashboardResponse getStudentDashboard(AgendaUserDetails currentUser) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime endOfWeek = now.plusDays(7);

        // 1. Pega os dados do boletim que refatoramos
        var reportCard = gradeService.getStudentReportCard(currentUser.getId(), currentUser);

        // 2. Busca os próximos 3 eventos no calendário
        var events = eventService.getCalendar(now, endOfWeek, currentUser)
                .stream().limit(3).toList();

        // 3. Conta mensagens não lidas
        long unread = messageService.countUnreadMessages(currentUser.getId());

        // 4. Busca a grade de horários de hoje
        var schedule = timetableService.getTodayScheduleForStudent(currentUser.getId());

        return new StudentDashboardResponse(
                reportCard.globalAverage(),
                calculateGlobalAttendance(reportCard.subjects()),
                events,
                unread,
                schedule,
                "Bem-vindo de volta! Você tem " + events.size() + " eventos esta semana."
        );
    }

    private BigDecimal calculateGlobalAttendance(List<SubjectSummaryResponse> subjects) {
        if (subjects.isEmpty()) return BigDecimal.valueOf(100);
        return subjects.stream()
                .map(SubjectSummaryResponse::attendancePercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(subjects.size()), 2, RoundingMode.HALF_UP);
    }
}
