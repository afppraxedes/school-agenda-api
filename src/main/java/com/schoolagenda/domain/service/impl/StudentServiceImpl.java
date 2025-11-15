// src/main/java/com/schoolagenda/domain/service/StudentServiceImpl.java
package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.CreateStudentRequest;
import com.schoolagenda.application.web.dto.response.StudentResponse;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.repository.StudentRepository;
import com.schoolagenda.domain.service.StudentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<StudentResponse> findAll() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StudentResponse> findById(Long id) {
        return studentRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Override
    public StudentResponse create(CreateStudentRequest studentRequest) {
        Student student = convertToEntity(studentRequest);
        Student savedStudent = studentRepository.save(student);
        return convertToResponse(savedStudent);
    }

    @Override
    public StudentResponse update(Long id, CreateStudentRequest studentRequest) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setFullName(studentRequest.getFullName());
                    existingStudent.setBirthDate(studentRequest.getBirthDate());
                    existingStudent.setClassName(studentRequest.getClassName());

                    if (studentRequest.getProfilePhoto() != null) {
                        existingStudent.setProfilePhoto(studentRequest.getProfilePhoto());
                    }

                    Student updatedStudent = studentRepository.save(existingStudent);
                    return convertToResponse(updatedStudent);
                })
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    @Override
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public List<StudentResponse> findByClassName(String className) {
        return studentRepository.findByClassName(className)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentResponse> findByFullNameContaining(String name) {
        return studentRepository.findByFullNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllClassNames() {
        return studentRepository.findAllDistinctClassNames();
    }

    @Override
    public long countByClassName(String className) {
        return studentRepository.countByClassName(className);
    }

    @Override
    public List<StudentResponse> findLatestStudents(int limit) {
        return studentRepository.findTop10ByOrderByRegistrationDateDesc()
                .stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponse convertToResponse(Student student) {
        int age = LocalDate.now().getYear() - student.getBirthDate().getYear();

        return new StudentResponse(
                student.getId(),
                student.getFullName(),
                student.getBirthDate(),
                student.getClassName(),
                student.getProfilePhoto(),
                student.getRegistrationDate(),
                age
        );
    }

    @Override
    public Student convertToEntity(CreateStudentRequest studentRequest) {
        Student student = new Student();
        student.setFullName(studentRequest.getFullName());
        student.setBirthDate(studentRequest.getBirthDate());
        student.setClassName(studentRequest.getClassName());
        student.setProfilePhoto(studentRequest.getProfilePhoto());
        return student;
    }
}