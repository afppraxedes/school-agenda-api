// src/main/java/com/schoolagenda/domain/service/TeacherClassService.java
package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.CreateTeacherClassRequest;
import com.schoolagenda.application.web.dto.response.TeacherClassResponse;

import java.util.List;

public interface TeacherClassService {

    // Create a new teacher-class relationship
    TeacherClassResponse createTeacherClass(CreateTeacherClassRequest request);

    // Get teacher-class by ID
    TeacherClassResponse getTeacherClassById(Long id);

    // Get all classes for a teacher
    List<TeacherClassResponse> getClassesByTeacher(Long teacherId);

    // Get all teachers for a class
    List<TeacherClassResponse> getTeachersByClass(String className);

    // Get specific teacher-class relationship
    TeacherClassResponse getTeacherClass(Long teacherId, String className);

    // Check if teacher-class relationship exists
    boolean teacherClassExists(Long teacherId, String className);

    // Update teacher-class relationship
    TeacherClassResponse updateTeacherClass(Long id, CreateTeacherClassRequest request);

    // Delete teacher-class by ID
    void deleteTeacherClass(Long id);

    // Delete specific teacher-class relationship
    void deleteTeacherClass(Long teacherId, String className);

    // Get class count for teacher
    long getClassCountByTeacher(Long teacherId);

    // Get all distinct class names
    List<String> getAllDistinctClassNames();
}