// src/main/java/com/schoolagenda/application/web/controller/ResponsibleStudentController.java
package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.GradeStudentDTO;
import com.schoolagenda.application.web.dto.request.ResponsibleStudentRequest;
import com.schoolagenda.application.web.dto.response.ResponsibleStudentResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.service.ResponsibleStudentService;
import com.schoolagenda.domain.service.impl.DependentServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/responsible-students")
public class ResponsibleStudentController {

    private final ResponsibleStudentService responsibleStudentService;
    private final DependentServiceImpl dependentService;

    public ResponsibleStudentController(ResponsibleStudentService responsibleStudentService, DependentServiceImpl dependentService) {
        this.responsibleStudentService = responsibleStudentService;
        this.dependentService = dependentService;
    }

    @PostMapping
    public ResponseEntity<ResponsibleStudentResponse> createRelationship(
            @Valid @RequestBody ResponsibleStudentRequest request) {
        ResponsibleStudentResponse response = responsibleStudentService.createRelationship(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRelationship(@PathVariable Long id) {
        responsibleStudentService.deleteRelationship(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/responsible/{responsibleId}/student/{studentId}")
    public ResponseEntity<Void> deleteRelationship(
            @PathVariable Long responsibleId, @PathVariable Long studentId) {
        responsibleStudentService.deleteRelationship(responsibleId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponsibleStudentResponse> getRelationshipById(@PathVariable Long id) {
        ResponsibleStudentResponse response = responsibleStudentService.getRelationshipById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/responsible/{responsibleId}")
    public ResponseEntity<List<ResponsibleStudentResponse>> getRelationshipsByResponsible(
            @PathVariable Long responsibleId) {
        List<ResponsibleStudentResponse> responses = responsibleStudentService.getRelationshipsByResponsible(responsibleId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ResponsibleStudentResponse>> getRelationshipsByStudent(
            @PathVariable Long studentId) {
        List<ResponsibleStudentResponse> responses = responsibleStudentService.getRelationshipsByStudent(studentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/responsible/{responsibleId}/student/{studentId}")
    public ResponseEntity<ResponsibleStudentResponse> getRelationship(
            @PathVariable Long responsibleId, @PathVariable Long studentId) {
        ResponsibleStudentResponse response = responsibleStudentService.getRelationship(responsibleId, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/responsible/{responsibleId}/student/{studentId}/exists")
    public ResponseEntity<Boolean> relationshipExists(
            @PathVariable Long responsibleId, @PathVariable Long studentId) {
        boolean exists = responsibleStudentService.relationshipExists(responsibleId, studentId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/responsible/{responsibleId}/student-count")
    public ResponseEntity<Long> getStudentCountByResponsible(@PathVariable Long responsibleId) {
        long count = responsibleStudentService.getStudentCountByResponsible(responsibleId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/dependents")
    @PreAuthorize("hasAuthority('RESPONSIBLE')")
    public ResponseEntity<List<GradeStudentDTO>> getMyDependents() {
        AgendaUserDetails user = (AgendaUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(dependentService.getDependentsPerformance(user.getId()));
    }
}