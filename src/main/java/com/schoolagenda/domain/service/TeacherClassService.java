// src/main/java/com/schoolagenda/domain/service/TeacherClassService.java
package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.TeacherClassRequest;
import com.schoolagenda.application.web.dto.response.TeacherClassResponse;

import java.util.List;

public interface TeacherClassService {

    // Create a new teacher-class relationship
    TeacherClassResponse createTeacherClass(TeacherClassRequest request);

    // Get teacher-class by ID
    TeacherClassResponse getTeacherClassById(Long id);

    // Get all classes for a teacher
    List<TeacherClassResponse> getClassesByTeacher(Long teacherId);

    // Get all teachers for a class
    List<TeacherClassResponse> getTeachersByClass(Long schoolClassId);

    // Get specific teacher-class relationship
    TeacherClassResponse getTeacherClass(Long teacherId, Long subjectId, Long schoolClassId);

    // Check if teacher-class relationship exists
    public boolean teacherClassExists(Long teacherId, Long subjectId, Long schoolClassId);

    // Update teacher-class relationship
    TeacherClassResponse updateTeacherClass(Long id, TeacherClassRequest request);

    // Delete teacher-class by ID
    void deleteTeacherClass(Long id);

    // Delete specific teacher-class relationship
    void deleteTeacherClass(Long teacherId, Long subjectId, Long schoolClassId);

    // Get class count for teacher
    long getClassCountByTeacher(Long teacherId);

    // Get all distinct class names
    List<Long> getAllDistinctSchoolClassIds();

    List<Long> findStudentIdsByResponsibleId(Long responsibleId);

    public List<Long> findSubjectIdsByTeacherId(Long teacherId);
}