package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.domain.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Gerenciamento de notas e resultados")
public class GradeController {

    private final GradeService gradeService;

    @PostMapping
    @Operation(summary = "Criar uma nova nota")
    public ResponseEntity<GradeResponse> create(@Valid @RequestBody GradeRequest request) {
        GradeResponse response = gradeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Criar múltiplas notas em lote")
    public ResponseEntity<GradeResponse> bulkCreate(@Valid @RequestBody List<GradeRequest> requests) {
        GradeResponse response = gradeService.bulkCreate(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota por ID")
    public ResponseEntity<GradeResponse> findById(@PathVariable Long id) {
        GradeResponse response = gradeService.findById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assessment/{assessmentId}/student/{studentId}")
    @Operation(summary = "Buscar nota por avaliação e estudante")
    public ResponseEntity<GradeResponse> findByAssessmentAndStudent(
            @PathVariable Long assessmentId,
            @PathVariable Long studentId) {

        GradeResponse response = gradeService.findByAssessmentAndStudent(assessmentId, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assessment/{assessmentId}")
    @Operation(summary = "Listar notas de uma avaliação")
    public ResponseEntity<List<GradeResponse>> findByAssessment(@PathVariable Long assessmentId) {
        List<GradeResponse> responses = gradeService.findByAssessment(assessmentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Listar notas de um estudante")
    public ResponseEntity<List<GradeResponse>> findByStudent(@PathVariable Long studentId) {
        List<GradeResponse> responses = gradeService.findByStudent(studentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @Operation(summary = "Listar notas de um estudante em uma disciplina")
    public ResponseEntity<List<GradeResponse>> findByStudentAndSubject(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {

        List<GradeResponse> responses = gradeService.findByStudentAndSubject(studentId, subjectId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/assessment/{assessmentId}/ungraded")
    @Operation(summary = "Listar estudantes sem nota em uma avaliação")
    public ResponseEntity<List<GradeResponse>> findUngradedByAssessment(@PathVariable Long assessmentId) {
        List<GradeResponse> responses = gradeService.findUngradedByAssessment(assessmentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}/average")
    @Operation(summary = "Calcular média do estudante em uma disciplina")
    public ResponseEntity<Double> calculateStudentAverage(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {

        Double average = gradeService.calculateStudentAverage(studentId, subjectId);
        return ResponseEntity.ok(average);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar nota")
    public ResponseEntity<GradeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody GradeRequest request) {

        GradeResponse response = gradeService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir nota")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gradeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
