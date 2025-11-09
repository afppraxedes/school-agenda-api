// src/main/java/com/schoolagenda/application/web/dto/TeacherClassResponse.java
package com.schoolagenda.application.web.dto.response;

import java.time.LocalDateTime;

public class TeacherClassResponse {

    private Long id;
    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
    private String className;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public TeacherClassResponse() {}

    public TeacherClassResponse(Long id, Long teacherId, String teacherName,
                                String teacherEmail, String className,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.teacherEmail = teacherEmail;
        this.className = className;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }

    public String getTeacherEmail() { return teacherEmail; }
    public void setTeacherEmail(String teacherEmail) { this.teacherEmail = teacherEmail; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}