package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.response.ActiveClassResponse;
import com.schoolagenda.application.web.dto.response.PerformanceHistoryResponse;
import com.schoolagenda.application.web.dto.response.TeacherAlertResponse;
import com.schoolagenda.application.web.dto.response.TeacherDashboardResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherDashboardService {

    // Injetando o seu serviço que já existe para buscar as turmas
    private final TeacherClassService teacherClassService;

    public TeacherDashboardService(TeacherClassService teacherClassService) {
        this.teacherClassService = teacherClassService;
    }

    public TeacherDashboardResponse getTeacherDashboardData(String email) {
        TeacherDashboardResponse dashboard = new TeacherDashboardResponse();

        // 1. Busca turmas reais usando seu serviço existente
        // Supondo que seu método retorne List<ActiveClassDTO> ou similar
        List<ActiveClassResponse> classes = teacherClassService.findClassesByTeacherEmail(email);

        // 2. Consolidação de dados básicos
        int totalStudents = classes.stream().mapToInt(ActiveClassResponse::getStudentCount).sum();

        dashboard.setWelcomeMessage("Olá, Professor!");
        dashboard.setTotalStudents(totalStudents);
        dashboard.setActiveClasses(classes);
        dashboard.setAverageClassPerformance(8.5); // Placeholder para cálculo futuro

        // 3. Lógica de Alertas (Implementada aqui diretamente para evitar classes inexistentes)
        dashboard.setAlerts(this.mapPendingAlerts(classes));
        dashboard.setPendingGradesCount(dashboard.getAlerts().size());

        // 4. Mock para o gráfico
        PerformanceHistoryResponse history = new PerformanceHistoryResponse();
        history.setLabels(List.of("Turma A", "Turma B", "Turma C"));
        history.setValues(List.of(8.0, 7.5, 9.0));
        dashboard.setPerformanceHistory(history);

        return dashboard;
    }

    private List<TeacherAlertResponse> mapPendingAlerts(List<ActiveClassResponse> classes) {
        List<TeacherAlertResponse> alerts = new ArrayList<>();
        // Lógica simples: se houver turmas, sugere revisão de notas
        if (!classes.isEmpty()) {
            TeacherAlertResponse alert = new TeacherAlertResponse();
            alert.setType("GRADE");
            alert.setMessage("Existem notas pendentes de lançamento para a turma " + classes.get(0).getName());
            alert.setPriority("HIGH");
            alert.setDeadline("Hoje");
            alerts.add(alert);
        }
        return alerts;
    }
}
