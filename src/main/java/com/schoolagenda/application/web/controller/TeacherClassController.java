// src/main/java/com/schoolagenda/application/web/controller/TeacherClassController.java
package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.TeacherClassRequest;
import com.schoolagenda.application.web.dto.response.TeacherClassResponse;
import com.schoolagenda.domain.service.TeacherClassService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher-classes")
public class TeacherClassController {

    @Autowired
    private TeacherClassService teacherClassService;

    @PostMapping
    public ResponseEntity<TeacherClassResponse> createTeacherClass(
            @Valid @RequestBody TeacherClassRequest request) {
        TeacherClassResponse response = teacherClassService.createTeacherClass(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherClassResponse> getTeacherClassById(@PathVariable Long id) {
        TeacherClassResponse response = teacherClassService.getTeacherClassById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<TeacherClassResponse>> getClassesByTeacher(
            @PathVariable Long teacherId) {
        List<TeacherClassResponse> responses = teacherClassService.getClassesByTeacher(teacherId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/class/{className}")
    public ResponseEntity<List<TeacherClassResponse>> getTeachersByClass(
            @PathVariable String className) {
        List<TeacherClassResponse> responses = teacherClassService.getTeachersByClass(className);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/teacher/{teacherId}/class/{className}")
    public ResponseEntity<TeacherClassResponse> getTeacherClass(
            @PathVariable Long teacherId, @PathVariable String className) {
        TeacherClassResponse response = teacherClassService.getTeacherClass(teacherId, className);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}/class/{className}/exists")
    public ResponseEntity<Boolean> teacherClassExists(
            @PathVariable Long teacherId, @PathVariable String className) {
        boolean exists = teacherClassService.teacherClassExists(teacherId, className);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/teacher/{teacherId}/class-count")
    public ResponseEntity<Long> getClassCountByTeacher(@PathVariable Long teacherId) {
        long count = teacherClassService.getClassCountByTeacher(teacherId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/classes/distinct")
    public ResponseEntity<List<String>> getAllDistinctClassNames() {
        List<String> classNames = teacherClassService.getAllDistinctClassNames();
        return ResponseEntity.ok(classNames);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherClassResponse> updateTeacherClass(
            @PathVariable Long id, @Valid @RequestBody TeacherClassRequest request) {
        TeacherClassResponse response = teacherClassService.updateTeacherClass(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacherClass(@PathVariable Long id) {
        teacherClassService.deleteTeacherClass(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/teacher/{teacherId}/class/{className}")
    public ResponseEntity<Void> deleteTeacherClass(
            @PathVariable Long teacherId, @PathVariable String className) {
        teacherClassService.deleteTeacherClass(teacherId, className);
        return ResponseEntity.noContent().build();
    }
}