package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.schoolagenda.application.web.dto.response.base.BaseAuditableResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeResponse extends BaseAuditableResponse {

    private Long id;
    private AssessmentSimpleResponse assessment;
    private UserSimpleResponse student;
    private UserSimpleResponse gradedBy;

    private BigDecimal score;
    private BigDecimal maxScore;
    private BigDecimal percentage;
    private String feedback;

    private Boolean absent;
    private Boolean excused;

    // @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
//    private LocalDateTime gradedAt;
    private OffsetDateTime gradedAt;

//    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
//    private LocalDateTime createdAt;
//
//    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
//    private LocalDateTime updatedAt;

    // Campos calculados
    private boolean graded;
    private boolean passing;

    // Métodos getter personalizados
    public boolean isGraded() {
        return score != null;
    }

    public boolean isPassing() {
        if (score == null || maxScore == null ||
                maxScore.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }

        BigDecimal passingThreshold = new BigDecimal("0.6");
        BigDecimal percentage = score.divide(maxScore, 4, RoundingMode.HALF_UP);
        return percentage.compareTo(passingThreshold) >= 0;
    }

    public BigDecimal getPercentage() {
        if (score == null || maxScore == null ||
                maxScore.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return score.divide(maxScore, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Para compatibilidade
    public Double getScoreAsDouble() {
        return score != null ? score.doubleValue() : null;
    }

    public Double getPercentageAsDouble() {
        BigDecimal percentage = getPercentage();
        return percentage != null ? percentage.doubleValue() : null;
    }
}
