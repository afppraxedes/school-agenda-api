package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.grade.GradeFilterRequest;
import com.schoolagenda.application.web.dto.request.GradeRequest;
import com.schoolagenda.application.web.dto.response.GradeResponse;
import com.schoolagenda.application.web.dto.response.ReportCardResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.enums.UserRole;
import com.schoolagenda.domain.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Gerenciamento de notas e resultados")
public class GradeController {

    private final GradeService gradeService;

//    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMINISTRATOR', 'DIRECTOR')")
//    @PostMapping
//    @Operation(summary = "Criar uma nova nota")
//    public ResponseEntity<GradeResponse> create(@Valid @RequestBody GradeRequest request) {
//        GradeResponse response = gradeService.create(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }

    /**
     * Lançamento de nota.
     * Permitido apenas para Professores, Diretores e Administradores.
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('TEACHER', 'DIRECTOR', 'ADMINISTRATOR')")
    public ResponseEntity<GradeResponse> create(
            @Valid @RequestBody GradeRequest request,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        GradeResponse response = gradeService.create(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

//    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMINISTRATOR', 'DIRECTOR')")
//    @PostMapping("/bulk")
//    @Operation(summary = "Criar múltiplas notas em lote")
//    public ResponseEntity<GradeResponse> bulkCreate(@Valid @RequestBody List<GradeRequest> requests, AgendaUserDetails currentUser) {
//        GradeResponse response = gradeService.bulkCreate(requests, currentUser);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMINISTRATOR', 'DIRECTOR')")
    public ResponseEntity<GradeResponse> bulkCreate(
            @Valid @RequestBody List<GradeRequest> requests,
            @AuthenticationPrincipal AgendaUserDetails currentUser) { // <--- Adicione esta anotação!

        GradeResponse response = gradeService.bulkCreate(requests, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualização de nota existente.
     * Apenas Professores, Diretores e Administradores.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'DIRECTOR', 'ADMINISTRATOR')")
    public ResponseEntity<GradeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody GradeRequest request,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        GradeResponse response = gradeService.update(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

//    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMINISTRATOR', 'DIRECTOR')")
//    @PutMapping("/{id}")
//    @Operation(summary = "Atualizar nota")
//    public ResponseEntity<GradeResponse> update(
//            @PathVariable Long id,
//            @Valid @RequestBody GradeRequest request) {
//
//        GradeResponse response = gradeService.update(id, request);
//        return ResponseEntity.ok(response);
//    }

//    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMINISTRATOR', 'DIRECTOR')")
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Excluir nota")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        gradeService.delete(id);
//        return ResponseEntity.noContent().build();
//    }

    // MÉTODO REFATORADO PELO "GEMINI". Limpar o arquivo!
    /**
     * Exclui uma nota existente.
     * Permitido apenas para Professores, Diretores e Administradores.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TEACHER', 'DIRECTOR', 'ADMINISTRATOR')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        gradeService.delete(id, currentUser);
    }

//    @GetMapping("/report-card/{studentUserId}")
//    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'ADMINISTRATOR') or " +
//            "(hasAuthority('STUDENT') and #studentUserId == authentication.principal.id) or " +
//            "hasAuthority('RESPONSIBLE')") // No Service validaremos se o estudante é filho do responsável
//    public ResponseEntity<ReportCardResponse> getReportCard(@PathVariable Long studentUserId,
//                                                            @AuthenticationPrincipal AgendaUserDetails currentUser) {
//
//        // Se for Responsável, validar se studentUserId pertence a um dos seus dependentes
//        if (currentUser.hasRole(UserRole.RESPONSIBLE)) {
//            validateRelationship(currentUser.getId(), studentUserId);
//        }
//
//        return ResponseEntity.ok(gradeService.getStudentReportCard(studentUserId));
//    }

    @GetMapping("/report-card/{studentUserId}")
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'ADMINISTRATOR', 'TEACHER', 'STUDENT', 'RESPONSIBLE')")
    public ResponseEntity<ReportCardResponse> getReportCard(
            @PathVariable Long studentUserId,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        // A validação de quem pode ver o quê ocorre dentro do Service
        return ResponseEntity.ok(gradeService.getStudentReportCard(studentUserId, currentUser));
    }

    // TODO: DAQUI PARA BAIXO VERIFICAR QUAIS TIPOS DE PERMISSÕES CADA RECURSO TERÁ!
    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota por ID")
    public ResponseEntity<GradeResponse> findById(@PathVariable Long id) {
        GradeResponse response = gradeService.findById(id);
        return ResponseEntity.ok(response);
    }

    // MÉTODOS COM PAGINAÇÃO E FILTRO
    // TODO: metodo anterior sem "RBAC"
//    @GetMapping("/search")
//    @Operation(summary = "Buscar notas com filtros avançados")
//    public ResponseEntity<PaginationResponse<GradeResponse>> search(
//            @Valid PaginationRequest pageRequest,
//            @ModelAttribute GradeFilterRequest filter) {
//
//        PaginationResponse<GradeResponse> response = gradeService.search(pageRequest, filter);
//        return ResponseEntity.ok(response);
//    }

    @PreAuthorize("hasAnyAuthority('TEACHER', 'ADMINISTRATOR', 'DIRECTOR')")
    @GetMapping("/search")
    @Operation(summary = "Buscar notas com filtros avançados")
    public ResponseEntity<PaginationResponse<GradeResponse>> searchGrades(
            @Valid PaginationRequest pageRequest,
            @ModelAttribute GradeFilterRequest filter,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        PaginationResponse<GradeResponse> response = gradeService.searchGrades(pageRequest, filter, currentUser);
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
