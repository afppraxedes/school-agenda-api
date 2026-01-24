package com.schoolagenda.domain.model;

import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "grades",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_grade_assessment_student",
                columnNames = {"assessment_id", "student_user_id"}
        ))
@Getter
@Setter
@NoArgsConstructor
public class Grade extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_user_id", nullable = false)
    private User student;

    // Se desejar que a auditoria preencha quem deu a nota, você pode remover este campo e usar o 'createdBy' da classe base.
    // Mas, manter o objeto User aqui é útil se o 'gradedBy' puder ser diferente do 'criador' do registro.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by_user_id")
    private User gradedBy;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "graded_at")
    private OffsetDateTime gradedAt;

    @Column(name = "is_absent")
    private Boolean absent = false;

    @Column(name = "is_excused")
    private Boolean excused = false;

//    @CreationTimestamp
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;

    // ========== MÉTODOS UTILITÁRIOS ==========

    public BigDecimal getPercentage() {
        if (score == null || maxScore == null ||
                maxScore.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        return score.divide(maxScore, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    public boolean isGraded() {
        return score != null;
    }

    public boolean isPassing() {
        if (score == null || maxScore == null) return false;

        // Considera aprovação com 60% ou mais
        BigDecimal passingThreshold = new BigDecimal("0.6");
        BigDecimal percentage = score.divide(maxScore, 4, RoundingMode.HALF_UP);
        return percentage.compareTo(passingThreshold) >= 0;
    }

    public BigDecimal getWeightedScore(BigDecimal weight) {
        if (score == null || weight == null) return null;
        return score.multiply(weight).setScale(2, RoundingMode.HALF_UP);
    }

    // Métodos de conveniência para Double (se necessário)
    public Double getScoreAsDouble() {
        return score != null ? score.doubleValue() : null;
    }

    public void setScoreAsDouble(Double value) {
        this.score = value != null ?
                BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP) :
                null;
    }

    public Double getPercentageAsDouble() {
        BigDecimal percentage = getPercentage();
        return percentage != null ? percentage.doubleValue() : null;
    }

    @PrePersist
    @PreUpdate
    public void prePersistUpdate() {
        // Copia maxScore da avaliação se não estiver definido
        if (this.maxScore == null && this.assessment != null) {
            this.maxScore = this.assessment.getMaxScore();
        }

        // Se ausente ou justificada, score deve ser null
        if (Boolean.TRUE.equals(this.absent) || Boolean.TRUE.equals(this.excused)) {
            this.score = null;
        }

        // Define gradedAt se foi avaliado agora
        if (this.gradedAt == null && this.score != null) {
            this.gradedAt = OffsetDateTime.now(ZoneOffset.UTC);
        }

//        this.updatedAt = LocalDateTime.now();
    }
}