package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.controller.doc.AssessmentControllerDoc;
import com.schoolagenda.application.web.dto.request.AssessmentRequest;
import com.schoolagenda.application.web.dto.response.AssessmentResponse;
import com.schoolagenda.domain.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
public class AssessmentController implements AssessmentControllerDoc {

    private final AssessmentService assessmentService;

    @Override
    @PostMapping
    public ResponseEntity<AssessmentResponse> create(@Valid @RequestBody AssessmentRequest request) {
        AssessmentResponse response = assessmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResponse> findById(@PathVariable Long id) {
        AssessmentResponse response = assessmentService.findById(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<AssessmentResponse>> findAll() {
        List<AssessmentResponse> responses = assessmentService.findAll();
        return ResponseEntity.ok(responses);
    }

    @Override
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<AssessmentResponse>> findBySubject(@PathVariable Long subjectId) {
        List<AssessmentResponse> responses = assessmentService.findBySubject(subjectId);
        return ResponseEntity.ok(responses);
    }

    @Override
    @GetMapping("/subject/{subjectId}/published")
    public ResponseEntity<List<AssessmentResponse>> findPublishedBySubject(@PathVariable Long subjectId) {
        List<AssessmentResponse> responses = assessmentService.findPublishedBySubject(subjectId);
        return ResponseEntity.ok(responses);
    }

    @Override
    @GetMapping("/published")
    public ResponseEntity<List<AssessmentResponse>> findPublishedByFilters(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<AssessmentResponse> responses = assessmentService.findPublishedByFilters(subjectId, startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @Override
    @GetMapping("/upcoming")
    public ResponseEntity<List<AssessmentResponse>> findUpcoming(
            @RequestParam(required = false, defaultValue = "30") Integer days) {

        List<AssessmentResponse> responses = assessmentService.findUpcoming(days);
        return ResponseEntity.ok(responses);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<AssessmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AssessmentRequest request) {

        AssessmentResponse response = assessmentService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assessmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}/publish")
    public ResponseEntity<AssessmentResponse> publish(@PathVariable Long id) {
        AssessmentResponse response = assessmentService.publish(id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{id}/unpublish")
    public ResponseEntity<AssessmentResponse> unpublish(@PathVariable Long id) {
        AssessmentResponse response = assessmentService.unpublish(id);
        return ResponseEntity.ok(response);
    }
}
