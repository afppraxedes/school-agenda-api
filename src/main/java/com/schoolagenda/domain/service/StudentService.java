// src/main/java/com/schoolagenda/domain/service/StudentService.java
package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.CreateStudentRequest;
import com.schoolagenda.application.web.dto.response.StudentResponse;
import com.schoolagenda.domain.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    // Basic CRUD operations
    List<StudentResponse> findAll();
    Optional<StudentResponse> findById(Long id);
    StudentResponse create(CreateStudentRequest studentRequest);
    StudentResponse update(Long id, CreateStudentRequest studentRequest);
    void delete(Long id);

    // Business logic operations
    List<StudentResponse> findByClassName(String className);
    List<StudentResponse>   findByFullNameContaining(String name);
    List<String> findAllClassNames();
    long countByClassName(String className);
    List<StudentResponse> findLatestStudents(int limit);

    // Utility methods
    StudentResponse convertToResponse(Student student);
    Student convertToEntity(CreateStudentRequest studentRequest);
}