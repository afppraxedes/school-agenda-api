// src/main/java/com/schoolagenda/application/web/dto/ResponsibleStudentResponse.java
package com.schoolagenda.application.web.dto.response;

import java.time.LocalDateTime;

public class ResponsibleStudentResponse {

    private Long id;
    private Long responsibleId;
    private String responsibleName;
    private String responsibleEmail;
    private Long studentId;
    private String studentName;
    private String studentClass;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ResponsibleStudentResponse() {}

    public ResponsibleStudentResponse(Long id, Long responsibleId, String responsibleName,
                                      String responsibleEmail, Long studentId, String studentName,
                                      String studentClass, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.responsibleId = responsibleId;
        this.responsibleName = responsibleName;
        this.responsibleEmail = responsibleEmail;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getResponsibleId() { return responsibleId; }
    public void setResponsibleId(Long responsibleId) { this.responsibleId = responsibleId; }

    public String getResponsibleName() { return responsibleName; }
    public void setResponsibleName(String responsibleName) { this.responsibleName = responsibleName; }

    public String getResponsibleEmail() { return responsibleEmail; }
    public void setResponsibleEmail(String responsibleEmail) { this.responsibleEmail = responsibleEmail; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentClass() { return studentClass; }
    public void setStudentClass(String studentClass) { this.studentClass = studentClass; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}