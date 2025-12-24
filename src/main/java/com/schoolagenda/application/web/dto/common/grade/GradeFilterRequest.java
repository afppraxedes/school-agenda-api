package com.schoolagenda.application.web.dto.common.grade;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeFilterRequest {

    private Long assessmentId;
    private Long studentId;
    private Long gradedById;
    private Long subjectId; // Filtro via assessment (útil para buscar todas notas de um aluno em uma disciplina)

    private BigDecimal minScore;
    private BigDecimal maxScore;

    private Boolean absent;
    private Boolean excused;
    private Boolean graded; // Se tem nota (score != null) ou não

    // Métodos auxiliares
    public boolean hasScoreRange() {
        return minScore != null || maxScore != null;
    }

    public boolean isScoreRangeValid() {
        if (minScore == null || maxScore == null) return true;
        return minScore.compareTo(maxScore) <= 0;
    }

    // Fábricas estáticas para casos comuns
    public static GradeFilterRequest forUngradedByAssessment(Long assessmentId) {
        GradeFilterRequest filter = new GradeFilterRequest();
        filter.setAssessmentId(assessmentId);
        filter.setGraded(false); // Notas não lançadas
        return filter;
    }

    public static GradeFilterRequest forStudentInSubject(Long studentId, Long subjectId) {
        GradeFilterRequest filter = new GradeFilterRequest();
        filter.setStudentId(studentId);
        filter.setSubjectId(subjectId);
        return filter;
    }

    public static GradeFilterRequest forPassingGrades(BigDecimal passingScore) {
        GradeFilterRequest filter = new GradeFilterRequest();
        filter.setMinScore(passingScore);
        return filter;
    }
}
