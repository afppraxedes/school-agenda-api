// src/main/java/com/schoolagenda/application/web/dto/ResponsibleStudentRequest.java
package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.NotNull;

public class ResponsibleStudentRequest {

    @NotNull(message = "Responsible ID is required")
    private Long responsibleId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

    // Constructors
    public ResponsibleStudentRequest() {}

    public ResponsibleStudentRequest(Long responsibleId, Long studentId) {
        this.responsibleId = responsibleId;
        this.studentId = studentId;
    }

    // Getters and Setters
    public Long getResponsibleId() { return responsibleId; }
    public void setResponsibleId(Long responsibleId) { this.responsibleId = responsibleId; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
}