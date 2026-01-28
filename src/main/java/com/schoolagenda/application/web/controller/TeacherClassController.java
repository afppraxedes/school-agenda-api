// src/main/java/com/schoolagenda/application/web/controller/TeacherClassController.java
package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.TeacherClassRequest;
import com.schoolagenda.application.web.dto.response.TeacherClassResponse;
import com.schoolagenda.application.web.dto.response.UserResponse;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.service.TeacherClassService;
import com.schoolagenda.domain.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher-classes")
public class TeacherClassController {

    private final TeacherClassService teacherClassService;
    private final UserService userService;

    public TeacherClassController(TeacherClassService teacherClassService, UserService userService) {
        this.teacherClassService = teacherClassService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<TeacherClassResponse> createTeacherClass(
            @Valid @RequestBody TeacherClassRequest request) {
        TeacherClassResponse response = teacherClassService.createTeacherClass(request);
        return ResponseEntity.ok(response);
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

    @DeleteMapping("/by-params")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTeacherClassByParams(
            @RequestParam Long teacherId,
            @RequestParam Long subjectId,
            @RequestParam Long schoolClassId) {

        teacherClassService.deleteTeacherClass(teacherId, subjectId, schoolClassId);
        return ResponseEntity.noContent().build();
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

    @GetMapping("/class/{schoolClassId}")
    public ResponseEntity<List<TeacherClassResponse>> getTeachersByClass(
            @PathVariable Long schoolClassId) {
        List<TeacherClassResponse> responses = teacherClassService.getTeachersByClass(schoolClassId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/teacher/{teacherId}/subject/{subjectId}/schoolClass/{schoolClassId}")
    public ResponseEntity<TeacherClassResponse> getTeacherClass(
            @PathVariable Long teacherId, @PathVariable Long subjectId, @PathVariable Long schoolClassId) {
        TeacherClassResponse response = teacherClassService.getTeacherClass(teacherId, subjectId, schoolClassId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher/{teacherId}/subject/{subjectId}/schoolClass/{schoolClassId}/exists")
    public ResponseEntity<Boolean> teacherClassExists(
            @PathVariable Long teacherId, @PathVariable Long subjectId, @PathVariable Long schoolClassId) {
        boolean exists = teacherClassService.teacherClassExists(teacherId, subjectId, schoolClassId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/teacher/{teacherId}/class-count")
    public ResponseEntity<Long> getClassCountByTeacher(@PathVariable Long teacherId) {
        long count = teacherClassService.getClassCountByTeacher(teacherId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/classes/distinct")
    public ResponseEntity<List<Long>> getAllDistinctSchoolClassIds() {
        List<Long> classNames = teacherClassService.getAllDistinctSchoolClassIds();
        return ResponseEntity.ok(classNames);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    @GetMapping(value = "/subjects")
    public ResponseEntity<List<Long>> findSubjectIdsByTeacherId(@RequestParam Long teacherId) {
        List<Long> techIds = teacherClassService.findSubjectIdsByTeacherId(teacherId);
        return ResponseEntity.ok(techIds);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR', 'TEACHER')")
    @GetMapping(value = "/students")
    public ResponseEntity<List<Long>> findStudentIdsByResponsibleId(@RequestParam Long responsibleId) {
        List<Long> responsibleIds = teacherClassService.findStudentIdsByResponsibleId(responsibleId);;
        return ResponseEntity.ok(responsibleIds);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getTeachers() {
        return ResponseEntity.ok(userService.findAllByProfile(UserRole.TEACHER.name()));
    }
}