// src/main/java/com/schoolagenda/domain/service/StudentServiceImpl.java
package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.StudentRequest;
import com.schoolagenda.application.web.dto.response.StudentResponse;
import com.schoolagenda.application.web.mapper.StudentMapper;
import com.schoolagenda.domain.exception.ResourceNotFoundException;
import com.schoolagenda.domain.model.Student;
import com.schoolagenda.domain.repository.SchoolClassRepository;
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
    private final SchoolClassRepository schoolClassRepository;
    private final StudentMapper studentMapper;

    public StudentServiceImpl(StudentRepository studentRepository, SchoolClassRepository schoolClassRepository, StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.schoolClassRepository = schoolClassRepository;
        this.studentMapper = studentMapper;
    }

    @Override
    public List<StudentResponse> findAll() {
        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StudentResponse> findById(Long id) {
        return studentRepository.findById(id)
                .map(studentMapper::toResponse);
    }

    @Override
    public StudentResponse create(StudentRequest studentRequest) {

        schoolClassRepository.findById(studentRequest.getSchoolClass().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Não existe uma classe com o ID: " + studentRequest.getSchoolClass().getId()));

        Student student = studentMapper.toEntity(studentRequest);
        Student savedStudent = studentRepository.save(student);

        return studentMapper.toResponse(savedStudent);
    }

    @Override
    public StudentResponse update(Long id, StudentRequest studentRequest) {
        return studentRepository.findById(id)
                .map(existingStudent -> {
                    existingStudent.setFullName(studentRequest.getFullName());
                    existingStudent.setBirthDate(studentRequest.getBirthDate());
                    existingStudent.setClassName(studentRequest.getClassName());

                    if (studentRequest.getProfilePhoto() != null) {
                        existingStudent.setProfilePhoto(studentRequest.getProfilePhoto());
                    }

                    studentMapper.updateEntity(studentRequest, existingStudent);
                    Student updatedStudent = studentRepository.save(existingStudent);

                    return studentMapper.toResponse(updatedStudent);
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
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentResponse> findByFullNameContaining(String name) {
        return studentRepository.findByFullNameContainingIgnoreCase(name)
                .stream()
                .map(studentMapper::toResponse)
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
                .map(studentMapper::toResponse)
                .collect(Collectors.toList());
    }

//    @Override
//    public StudentResponse convertToResponse(Student student) {
//        int age = LocalDate.now().getYear() - student.getBirthDate().getYear();
//
//        return new StudentResponse(
//                student.getId(),
//                student.getFullName(),
//                student.getBirthDate(),
//                student.getClassName(),
//                student.getProfilePhoto(),
//                student.getRegistrationDate(),
//                age
//        );
//    }

//    @Override
//    public Student convertToEntity(StudentRequest studentRequest) {
//        Student student = new Student();
//        student.setFullName(studentRequest.getFullName());
//        student.setBirthDate(studentRequest.getBirthDate());
//        student.setClassName(studentRequest.getClassName());
//        student.setProfilePhoto(studentRequest.getProfilePhoto());
//        return student;
//    }
}