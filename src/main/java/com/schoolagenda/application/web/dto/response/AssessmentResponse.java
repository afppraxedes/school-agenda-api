package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.schoolagenda.application.web.dto.response.base.BaseAuditableResponse;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

//@Data
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class AssessmentResponse extends BaseAuditableResponse {

    private Long id;
    private String title;
    private String description;
    private SubjectSimpleResponse subject;
//    private UserSimpleResponse createdBy;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;

    private BigDecimal maxScore;
    private Boolean published;

//    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
//    private LocalDateTime createdAt;
//
//    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
//    private LocalDateTime updatedAt;

    // Campos calculados
    private boolean overdue;
    private boolean active;

//    private String createdBy;
//    private String lastModifiedBy;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

    public AssessmentResponse(Long id, String title, String description, SubjectSimpleResponse subject,
                              LocalDate dueDate, BigDecimal maxScore, Boolean published, boolean overdue, boolean active
                              /*String createdBy, String lastModifiedBy, LocalDateTime createdAt, LocalDateTime updatedAt*/) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
        this.published = published;
        this.overdue = overdue;
        this.active = active;
//        this.createdBy = createdBy;
//        this.lastModifiedBy = lastModifiedBy;
//        this.createdAt = createdAt;
//        this.updatedAt = updatedAt;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SubjectSimpleResponse getSubject() {
        return subject;
    }

    public void setSubject(SubjectSimpleResponse subject) {
        this.subject = subject;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public void setOverdue(boolean overdue) {
        this.overdue = overdue;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

//    public String getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(String createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    public String getLastModifiedBy() {
//        return lastModifiedBy;
//    }
//
//    public void setLastModifiedBy(String lastModifiedBy) {
//        this.lastModifiedBy = lastModifiedBy;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
}
