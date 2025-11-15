// src/main/java/com/schoolagenda/application/web/dto/TeacherClassRequest.java
package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateTeacherClassRequest {

    @NotNull(message = "Teacher ID is required")
    private Long teacherId;

    @NotBlank(message = "Class name is required")
    @Size(max = 100, message = "Class name must not exceed 100 characters")
    private String className;

    // Constructors
    public CreateTeacherClassRequest() {}

    public CreateTeacherClassRequest(Long teacherId, String className) {
        this.teacherId = teacherId;
        this.className = className;
    }

    // Getters and Setters
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}