package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.grade.GradeFilterRequest;
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

import java.math.BigDecimal;
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

    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota por ID")
    public ResponseEntity<GradeResponse> findById(@PathVariable Long id) {
        GradeResponse response = gradeService.findById(id);
        return ResponseEntity.ok(response);
    }

    // MÉTODOS COM PAGINAÇÃO E FILTRO
    @GetMapping("/search")
    @Operation(summary = "Buscar notas com filtros avançados")
    public ResponseEntity<PaginationResponse<GradeResponse>> search(
            @Valid PaginationRequest pageRequest,
            @ModelAttribute GradeFilterRequest filter) {

        PaginationResponse<GradeResponse> response = gradeService.search(pageRequest, filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assessment/{assessmentId}")
    @Operation(summary = "Listar notas de uma avaliação")
    public ResponseEntity<PaginationResponse<GradeResponse>> findByAssessment(
            @PathVariable Long assessmentId,
            @Valid PaginationRequest pageRequest) {

        PaginationResponse<GradeResponse> response =
                gradeService.findByAssessment(pageRequest, assessmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Listar notas de um estudante")
    public ResponseEntity<PaginationResponse<GradeResponse>> findByStudent(
            @PathVariable Long studentId,
            @Valid PaginationRequest pageRequest) {

        PaginationResponse<GradeResponse> response =
                gradeService.findByStudent(pageRequest, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/assessment/{assessmentId}/ungraded")
    @Operation(summary = "Listar estudantes não avaliados em uma avaliação")
    public ResponseEntity<PaginationResponse<GradeResponse>> findUngradedByAssessment(
            @PathVariable Long assessmentId,
            @Valid PaginationRequest pageRequest) {

        PaginationResponse<GradeResponse> response =
                gradeService.findUngradedByAssessment(pageRequest, assessmentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/passing")
    @Operation(summary = "Listar notas de aprovação")
    public ResponseEntity<PaginationResponse<GradeResponse>> findPassingGrades(
            @RequestParam(defaultValue = "6.0") BigDecimal passingScore,
            @Valid PaginationRequest pageRequest) {

        PaginationResponse<GradeResponse> response =
                gradeService.findPassingGrades(pageRequest, passingScore);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @Operation(summary = "Listar notas de um estudante em uma disciplina")
    public ResponseEntity<PaginationResponse<GradeResponse>> findByStudentAndSubject(
            @PathVariable Long studentId,
            @PathVariable Long subjectId,
            @Valid PaginationRequest pageRequest) {

        PaginationResponse<GradeResponse> response =
                gradeService.findByStudentAndSubject(pageRequest, studentId, subjectId);
        return ResponseEntity.ok(response);
    }
    // FIM MÉTODOS COM PAGINAÇÃO E FILTRO

    @GetMapping("/assessment/{assessmentId}/student/{studentId}")
    @Operation(summary = "Buscar nota por avaliação e estudante")
    public ResponseEntity<GradeResponse> findByAssessmentAndStudent(
            @PathVariable Long assessmentId,
            @PathVariable Long studentId) {

        GradeResponse response = gradeService.findByAssessmentAndStudent(assessmentId, studentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}/average")
    @Operation(summary = "Calcular média do estudante em uma disciplina")
    public ResponseEntity<Double> calculateStudentAverage(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {

        Double average = gradeService.calculateStudentAverage(studentId, subjectId);
        return ResponseEntity.ok(average);
    }

    // TODO: Verificar na refatpração se estes métodos permanecerão. Mas essa verificação só pode ser
    // efetuada após eftuar a integração com o "Frontend" para saber se há necessidade de utilizção!
    // Endpoints legacy para caso seja necessário
    // --> possui paginado
//    @GetMapping("/assessment/{assessmentId}")
//    @Operation(summary = "Listar notas de uma avaliação")
//    public ResponseEntity<List<GradeResponse>> findByAssessment(@PathVariable Long assessmentId) {
//        List<GradeResponse> responses = gradeService.findByAssessment(assessmentId);
//        return ResponseEntity.ok(responses);
//    }

    // --> possui paginado
//    @GetMapping("/student/{studentId}")
//    @Operation(summary = "Listar notas de um estudante")
//    public ResponseEntity<List<GradeResponse>> findByStudent(@PathVariable Long studentId) {
//        List<GradeResponse> responses = gradeService.findByStudent(studentId);
//        return ResponseEntity.ok(responses);
//    }

    // --> possui paginado
//    @GetMapping("/assessment/{assessmentId}/ungraded")
//    @Operation(summary = "Listar estudantes sem nota em uma avaliação")
//    public ResponseEntity<List<GradeResponse>> findUngradedByAssessment(@PathVariable Long assessmentId) {
//        List<GradeResponse> responses = gradeService.findUngradedByAssessment(assessmentId);
//        return ResponseEntity.ok(responses);
//    }

    // --> possui paginado
//    @GetMapping("/student/{studentId}/subject/{subjectId}")
//    @Operation(summary = "Listar notas de um estudante em uma disciplina")
//    public ResponseEntity<List<GradeResponse>> findByStudentAndSubject(
//            @PathVariable Long studentId,
//            @PathVariable Long subjectId) {
//
//        List<GradeResponse> responses = gradeService.findByStudentAndSubject(studentId, subjectId);
//        return ResponseEntity.ok(responses);
//    }
}
