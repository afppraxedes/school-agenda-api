package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.response.*;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.repository.AttendanceRepository;
import com.schoolagenda.domain.repository.EvaluationRepository;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.service.*;
import jakarta.persistence.EntityNotFoundException;
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

    private final EvaluationRepository evaluationRepository;
    private final AttendanceRepository attendanceRepository;
    private final MessageService messageService;
    private final ScheduleService scheduleService;
    private final EventService eventService;
    private final StudentRepository studentRepository;
    // Removi os serviços que ainda não existem para o código compilar

//    @Transactional(readOnly = true)
//    public StudentDashboardResponse getStudentDashboard(AgendaUserDetails currentUser) {
//        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
//        OffsetDateTime endOfWeek = now.plusDays(7);
//
//        // 1. Pega os dados do boletim que refatoramos
//        var reportCard = gradeService.getStudentReportCard(currentUser.getId(), currentUser);
//
//        // 2. Busca os próximos 3 eventos no calendário
//        var events = eventService.getCalendar(now, endOfWeek, currentUser)
//                .stream().limit(3).toList();
//
//        // 3. Conta mensagens não lidas
//        long unread = messageService.countUnreadMessages(currentUser.getId());
//
//        // 4. Busca a grade de horários de hoje
//        var schedule = timetableService.getTodayScheduleForStudent(currentUser.getId());
//
//        return new StudentDashboardResponse(
//                reportCard.globalAverage(),
//                calculateGlobalAttendance(reportCard.subjects()),
//                events,
//                unread,
//                schedule,
//                "Bem-vindo de volta! Você tem " + events.size() + " eventos esta semana."
//        );
//    }

//    public StudentDashboardResponse getStudentDashboard(Long userId) {
//        // Busca o ID do Aluno vinculado ao Usuário (Lógica de ID 23 -> ID 1)
//        Student student = studentRepository.findByUserId(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado"));
//        Long studentId = student.getId();
//
//        // 1. Médias e Frequência (Queries do Repository)
//        Double avg = evaluationRepository.calculateAverageByStudent(studentId);
//        BigDecimal globalAverage = BigDecimal.valueOf(avg != null ? avg : 0.0);
//
////        Integer att = attendanceRepository.calculatePercentageByStudent(studentId);
////        BigDecimal globalAttendance = BigDecimal.valueOf(att != null ? att : 0.0);
//
//        long present = attendanceRepository.countPresentDays(studentId);
//        long total = attendanceRepository.countTotalDays(studentId);
//
//        // Cálculo seguro no Java para evitar divisão por zero
//        BigDecimal globalAttendance = (total > 0)
//                ? BigDecimal.valueOf((present * 100.0) / total)
//                : BigDecimal.ZERO;
//
//        // 2. Histórico para o Chart.js
//        List<MonthlyAverageDTO> performanceHistory = evaluationRepository.findMonthlyAveragesByStudent(studentId);
//
//        // 3. Chamadas aos seus novos Serviços
//        List<TimetableResponse> todaysSchedule = scheduleService.findTodayByStudent(studentId);
//        List<SchoolEventResponse> upcomingEvents = eventService.findUpcomingByStudent(studentId);
//
//        // REUSO DO SEU MÉTODO EXISTENTE
//        long unreadMessages = messageService.countUnreadMessages(userId);
//
//        return new StudentDashboardResponse(
//                globalAverage,
//                globalAttendance,
//                upcomingEvents,
//                unreadMessages,
//                todaysSchedule,
//                "Bem-vindo ao seu painel escolar!",
//                performanceHistory
//        );
//    }

    public StudentDashboardResponse getStudentDashboard(Long userId) {
        // Busca o ID do Aluno vinculado ao Usuário (Lógica de ID 23 -> ID 1)
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Estudante não encontrado"));
        Long studentId = student.getId();

        // 1. Médias e Frequência (Queries do Repository)
        Double avg = evaluationRepository.calculateAverageByStudent(studentId);
        BigDecimal globalAverage = BigDecimal.valueOf(avg != null ? avg : 0.0);

//        Integer att = attendanceRepository.calculatePercentageByStudent(studentId);
//        BigDecimal globalAttendance = BigDecimal.valueOf(att != null ? att : 0.0);

        long present = attendanceRepository.countPresentDays(studentId);
        long total = attendanceRepository.countTotalDays(studentId);

        // Cálculo seguro no Java para evitar divisão por zero
        BigDecimal globalAttendance = (total > 0)
                ? BigDecimal.valueOf((present * 100.0) / total)
                : BigDecimal.ZERO;

        // 2. Histórico para o Chart.js
        List<MonthlyAverageDTO> performanceHistory = evaluationRepository.findMonthlyAveragesByStudent(studentId);

        // 3. Chamadas aos seus novos Serviços
        List<SchoolEventResponse> upcomingEvents = eventService.findUpcomingByStudent(studentId);
        List<TimetableResponse> todaysSchedule = scheduleService.findTodayByStudent(studentId);

        // REUSO DO SEU MÉTODO EXISTENTE
        long unreadMessages = messageService.countUnreadMessages(userId);

        return new StudentDashboardResponse(
                globalAverage,
                globalAttendance,
                upcomingEvents,
                unreadMessages,
                todaysSchedule,
                "Bem-vindo ao seu painel escolar!",
                performanceHistory
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
