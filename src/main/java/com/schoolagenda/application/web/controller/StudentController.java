// src/main/java/com/schoolagenda/application/web/controller/StudentController.java
package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.StudentRequest;
import com.schoolagenda.application.web.dto.response.StudentDetailResponse;
import com.schoolagenda.application.web.dto.response.StudentResponse;
import com.schoolagenda.domain.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest studentRequest) {
        StudentResponse createdStudent = studentService.create(studentRequest);
        return ResponseEntity.ok(createdStudent);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest studentRequest) {
        try {
            StudentResponse updatedStudent = studentService.update(id, studentRequest);
            return ResponseEntity.ok(updatedStudent);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        try {
            studentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER', 'RESPONSIBLE')")
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        List<StudentResponse> students = studentService.findAll();
        return ResponseEntity.ok(students);
    }

    // TODO: Os métodos "findById" e "getStudentById" estão com o mesmo endpoint, verificar qual é o mais adequado para manter!
//    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER', 'RESPONSIBLE')")
//    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
//        return studentService.findById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    @GetMapping("/class/{className}")
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER', 'RESPONSIBLE')")
    public ResponseEntity<List<StudentResponse>> getStudentsByClass(@PathVariable String className) {
        List<StudentResponse> students = studentService.findByClassName(className);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER', 'RESPONSIBLE')")
    public ResponseEntity<List<StudentResponse>> searchStudentsByName(@RequestParam String name) {
        List<StudentResponse> students = studentService.findByFullNameContaining(name);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/classes")
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER')")
    public ResponseEntity<List<String>> getAllClassNames() {
        List<String> classNames = studentService.findAllClassNames();
        return ResponseEntity.ok(classNames);
    }

    @GetMapping("/class/{className}/count")
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER')")
    public ResponseEntity<Long> getStudentCountByClass(@PathVariable String className) {
        long count = studentService.countByClassName(className);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/latest")
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER')")
    public ResponseEntity<List<StudentResponse>> getLatestStudents(@RequestParam(defaultValue = "10") int limit) {
        List<StudentResponse> students = studentService.findLatestStudents(limit);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'RESPONSIBLE', 'ADMINISTRATOR')")
    public ResponseEntity<StudentDetailResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }
}
