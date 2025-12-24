package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.controller.doc.AssessmentControllerDoc;
import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.assessment.AssessmentFilterRequest;
import com.schoolagenda.application.web.dto.request.AssessmentRequest;
import com.schoolagenda.application.web.dto.response.AssessmentResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
public class AssessmentController implements AssessmentControllerDoc {

    private final AssessmentService assessmentService;

    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER')")
    @PostMapping
    public ResponseEntity<AssessmentResponse> createAssessment(
            @Valid @RequestBody AssessmentRequest assessmentRequest) {
        AssessmentResponse response = assessmentService.create(assessmentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<AssessmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AssessmentRequest request) {

        AssessmentResponse response = assessmentService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        assessmentService.delete(id);
        return ResponseEntity.noContent().build();
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
    @GetMapping("/subject/{subjectId}/published")
    public ResponseEntity<List<AssessmentResponse>> findPublishedBySubject(@PathVariable Long subjectId) {
        List<AssessmentResponse> responses = assessmentService.findPublishedBySubject(subjectId);
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

    // Paginação
    @Override
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER', 'STUDENT', 'RESPONSIBLE')")
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<AssessmentResponse>> search(
            @Valid PaginationRequest pageRequest,
            @ModelAttribute AssessmentFilterRequest filter,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        // Validação manual
//        if (pageRequest == null) {
//            pageRequest = new PaginationRequest();
//        }
//        if (pageRequest.getSortBy() == null) {
//            pageRequest.setSortBy("id");
//        }
//        if (pageRequest.getDirection() == null) {
////            pageRequest.setDirection("ASC");
//            pageRequest.setDirection(Sort.Direction.ASC);
//        }

        PaginationResponse<AssessmentResponse> response =
                assessmentService.search(pageRequest, filter, currentUser);
        return ResponseEntity.ok(response);
    }

    // TODO: Verificar qual utilizar: A listagem com "paginação" (abaixo) ou sem "paginação" (mais abaixo)
    @Override
    @GetMapping("/published")
    public ResponseEntity<PaginationResponse<AssessmentResponse>> findPublished(
            @Valid PaginationRequest pageRequest) {

        PaginationResponse<AssessmentResponse> response = assessmentService.findPublished(pageRequest);
        return ResponseEntity.ok(response);
    }

//    @Override
//    @GetMapping("/published")
//    public ResponseEntity<List<AssessmentResponse>> findPublishedByFilters(
//            @RequestParam(required = false) Long subjectId,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
//
//        List<AssessmentResponse> responses = assessmentService.findPublishedByFilters(subjectId, startDate, endDate);
//        return ResponseEntity.ok(responses);
//    }

    // TODO: Verificar qual utilizar: A listagem com "paginação" (abaixo) ou sem "paginação" (mais abaixo)
    @Override
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<PaginationResponse<AssessmentResponse>> findBySubject(
            @PathVariable Long subjectId,
            @Valid PaginationRequest pageRequest) {

        PaginationResponse<AssessmentResponse> response =
                assessmentService.findBySubject(pageRequest, subjectId);
        return ResponseEntity.ok(response);
    }

//    @Override
//    @GetMapping("/subject/{subjectId}")
//    public ResponseEntity<List<AssessmentResponse>> findBySubject(@PathVariable Long subjectId) {
//        List<AssessmentResponse> responses = assessmentService.findBySubject(subjectId);
//        return ResponseEntity.ok(responses);
//    }
}
