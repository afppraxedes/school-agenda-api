// src/main/java/com/schoolagenda/domain/service/StudentService.java
package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.StudentRequest;
import com.schoolagenda.application.web.dto.response.StudentDashboardResponse;
import com.schoolagenda.application.web.dto.response.StudentDetailResponse;
import com.schoolagenda.application.web.dto.response.StudentResponse;
import com.schoolagenda.domain.model.Student;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StudentService {

    // Basic CRUD operations
    List<StudentResponse> findAll();
    Optional<StudentResponse> findById(Long id);
    StudentResponse create(StudentRequest studentRequest);
    StudentResponse update(Long id, StudentRequest studentRequest);
    void delete(Long id);

    // Business logic operations
    List<StudentResponse> findByClassName(String className);
    List<StudentResponse>   findByFullNameContaining(String name);
//    List<Long> findStudentIdsByResponsibleId(Long responsibleId);
    List<String> findAllClassNames();
    long countByClassName(String className);
    List<StudentResponse> findLatestStudents(int limit);

    void updateGlobalAverage(Long userId, BigDecimal rawAverage);

    StudentDetailResponse getStudentById(Long id);

//    StudentDashboardResponse getDashboardData(Long userId);

    // Utility methods
//    StudentResponse convertToResponse(Student student);
//    Student convertToEntity(StudentRequest studentRequest);
}