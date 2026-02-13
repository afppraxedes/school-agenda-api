package com.schoolagenda.domain.model;

import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
@Getter
@Setter
@NoArgsConstructor
// 4. Herda da classe base para obter os campos created_by, updated_at, etc.
public class Assessment extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(columnDefinition = "BIGSERIAL")
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_class_id", nullable = false)
    private TeacherClass teacherClass;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by_user_id")
//    private User createdBy;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "max_score", precision = 5, scale = 2, nullable = false)
    private BigDecimal maxScore = new BigDecimal("10.00");

    // NOVO CAMPO: Peso da avaliação para o cálculo de médias
    @Column(name = "weight", precision = 5, scale = 2, nullable = false)
    private BigDecimal weight = BigDecimal.ONE; // Valor padrão 1.0

    @Column(name = "is_published")
    private Boolean published = false;

    @Column(nullable = false)
    private boolean isRecovery = false;

//    @CreationTimestamp
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;

    // Métodos utilitários
    public BigDecimal getDefaultMaxScore() {
        return new BigDecimal("10.00");
    }

    public boolean isOverdue() {
        if (dueDate == null) return false;
        return LocalDate.now().isAfter(dueDate);
    }

    @PrePersist
    public void prePersist() {
        if (this.maxScore == null) {
            this.maxScore = getDefaultMaxScore();
        }
    }

//    @PreUpdate
//    public void preUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }
}