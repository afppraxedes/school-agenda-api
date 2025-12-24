package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.common.PaginationRequest;
import com.schoolagenda.application.web.dto.common.PaginationResponse;
import com.schoolagenda.application.web.dto.common.subject.SubjectFilterRequest;
import com.schoolagenda.application.web.dto.request.SubjectRequest;
import com.schoolagenda.application.web.dto.response.SubjectResponse;
import com.schoolagenda.domain.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
@Tag(name = "Disciplinas", description = "Gerenciamento de disciplinas")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    @Operation(summary = "Criar uma nova disciplina")
    public ResponseEntity<SubjectResponse> create(@Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar disciplina por ID")
    public ResponseEntity<SubjectResponse> findById(@PathVariable Long id) {
        SubjectResponse response = subjectService.findById(id);
        return ResponseEntity.ok(response);
    }

    // Paginação
//    @GetMapping("/search")
//    @Operation(summary = "Buscar disciplinas com filtros avançados")
//    public ResponseEntity<PaginationResponse<SubjectResponse>> search(
//            @Valid PaginationRequest pageRequest,
//            @Valid @ModelAttribute SubjectFilterRequest filter) {
//
//        // Exemplo: ordenação personalizada se não foi fornecida
//        if (pageRequest.getSortOrders().isEmpty()) {
//            pageRequest.addSort("name", Sort.Direction.ASC);
//            pageRequest.addSort("id", Sort.Direction.ASC);
//        }
//
//        PaginationResponse<SubjectResponse> response = subjectService.search(filter, pageRequest);
//        return ResponseEntity.ok(response);
//    }

    // TODO: O "@PreAuthorize" abaixo é apenas para testes!
    @PreAuthorize("hasAnyAuthority('DIRECTOR', 'TEACHER', 'STUDENT', 'RESPONSIBLE')")
//    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'DIRECTOR', 'TEACHER')")
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<SubjectResponse>> search(
            @Valid PaginationRequest pageRequest,
            @ModelAttribute SubjectFilterRequest filter
            /*@AuthenticationPrincipal AgendaUserDetails currentUser*/) {

        // Validação manual
        if (pageRequest == null) {
            pageRequest = new PaginationRequest();
        }
        if (pageRequest.getSortBy() == null) {
            pageRequest.setSortBy("id");
        }
        if (pageRequest.getDirection() == null) {
//            pageRequest.setDirection("ASC");
            pageRequest.setDirection(Sort.Direction.ASC);
        }

        PaginationResponse<SubjectResponse> response = subjectService.search(filter, pageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todas as disciplinas")
    public ResponseEntity<List<SubjectResponse>> findAll() {
        List<SubjectResponse> responses = subjectService.findAll();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Listar disciplinas de um professor")
    public ResponseEntity<List<SubjectResponse>> findByTeacher(@PathVariable Long teacherId) {
        List<SubjectResponse> responses = subjectService.findByTeacher(teacherId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/school-year/{schoolYear}")
    @Operation(summary = "Listar disciplinas por ano letivo")
    public ResponseEntity<List<SubjectResponse>> findBySchoolYear(@PathVariable String schoolYear) {
        List<SubjectResponse> responses = subjectService.findBySchoolYear(schoolYear);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    @Operation(summary = "Listar disciplinas ativas")
    public ResponseEntity<List<SubjectResponse>> findActive() {
        List<SubjectResponse> responses = subjectService.findActive();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar disciplina")
    public ResponseEntity<SubjectResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir disciplina")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Alternar status ativo/inativo")
    public ResponseEntity<SubjectResponse> toggleStatus(@PathVariable Long id) {
        SubjectResponse response = subjectService.toggleStatus(id);
        return ResponseEntity.ok(response);
    }
}
