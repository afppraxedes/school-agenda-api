package com.schoolagenda.application.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO: Renomear para "ActiveClassDTO" e colocar num outro pacote "dto" para manter a organização!
public class TeacherDashboardResponse {
    private String welcomeMessage;
    private Integer totalStudents;
    private Double averageClassPerformance;
    private Integer pendingGradesCount;
    private List<ActiveClassResponse> activeClasses;
    private List<TeacherAlertResponse> alerts;
    private PerformanceHistoryResponse performanceHistory;

    // Getters e Setters (ou use Lombok @Data)
}
