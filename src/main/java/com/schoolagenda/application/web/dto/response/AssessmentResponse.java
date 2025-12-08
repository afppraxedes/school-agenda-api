package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResponse {

    private Long id;
    private String title;
    private String description;
    private SubjectSimpleResponse subject;
    private UserSimpleResponse createdBy;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;

    private BigDecimal maxScore;
    private Boolean published;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime updatedAt;

    // Campos calculados
    private boolean overdue;
    private boolean active;

    // Métodos getter personalizados
    public boolean isOverdue() {
        if (dueDate == null) return false;
        return LocalDate.now().isAfter(dueDate);
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(published) &&
                (dueDate == null || !dueDate.isBefore(LocalDate.now()));
    }

    // Para compatibilidade
    public Double getMaxScoreAsDouble() {
        return maxScore != null ? maxScore.doubleValue() : null;
    }
}
