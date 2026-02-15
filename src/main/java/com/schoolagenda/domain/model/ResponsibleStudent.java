package com.schoolagenda.domain.model;

import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "responsible_student")
// 4. Herda da classe base para obter os campos created_by, updated_at, etc.
public class ResponsibleStudent /*extends BaseAuditableEntity*/ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id", nullable = false)
    private User responsible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // TODO: refatorar as "datas do vínculo (created_at e updated_at) para:
    // Timestamps do vínculo
//    @CreationTimestamp
//    @Column(name = "linked_at", updatable = false)
//    private LocalDateTime linkedAt;
//
//    @UpdateTimestamp
//    @Column(name = "relationship_updated_at")
//    private LocalDateTime relationshipUpdatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public ResponsibleStudent() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ResponsibleStudent(User responsible, Student student) {
        this();
        this.responsible = responsible;
        this.student = student;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getResponsible() { return responsible; }
    public void setResponsible(User responsible) {
        this.responsible = responsible;
        this.updatedAt = LocalDateTime.now();
    }

    public Student getStudent() { return student; }
    public void setStudent(Student student) {
        this.student = student;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}