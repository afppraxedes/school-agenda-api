package com.schoolagenda.application.web.controller.doc;

import com.schoolagenda.application.web.dto.request.AssessmentRequest;
import com.schoolagenda.application.web.dto.response.AssessmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// TODO: Verificar a versão do "swagger" e "spring-doc" quando for implementar toda documentação, pois parece
// estar numa versão "anterior" ("/api-docs/" e não "/v3/api-docs/"!)

// TODO: NÃO ESQUECER DE COLOCAR NOVAMENTE OS MÉTODOS "HTTP" NO "CONTROLLER" E VERIFICAR SE A NOMENCLATURA FICARÁ:
// NOTA: BASTA OLHAR A ÚLTIMA CONVERSA NO "DEEPSEEK"!
// - AssessmentControllerDoc (documentação)
// - AssessmentController (implementação)
@Tag(name = "Avaliações", description = "Gerenciamento de avaliações e atividades")
public interface AssessmentControllerDoc {

    // TODO: OLHAR NO "AuthenticationController" PARA DEIXAR A DOCUMENTAÇÃO MAIS COMPLETA (É EXEMPLO DO "FBE"!),
    // COLOCANDO OS "@ApiResponse", "@RequestParam" ENTRE OUTROS!
    @Operation(summary = "Criar uma nova avaliação")
    ResponseEntity<AssessmentResponse> create(@Valid @RequestBody AssessmentRequest request);

    @Operation(summary = "Buscar avaliação por ID")
    ResponseEntity<AssessmentResponse> findById(@PathVariable Long id);

    @Operation(summary = "Listar todas as avaliações")
    ResponseEntity<List<AssessmentResponse>> findAll();

    @Operation(summary = "Listar avaliações de uma disciplina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações (pode ser vazia)"),
            @ApiResponse(responseCode = "404", description = "Disciplina não encontrada")
    })
    ResponseEntity<List<AssessmentResponse>> findBySubject(@PathVariable Long subjectId);

    @Operation(summary = "Listar avaliações publicadas de uma disciplina")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações publicadas (pode ser vazia)"),
            @ApiResponse(responseCode = "404", description = "Disciplina não encontrada")
    })
    ResponseEntity<List<AssessmentResponse>> findPublishedBySubject(@PathVariable Long subjectId);

    @Operation(summary = "Listar avaliações publicadas com filtros")
    ResponseEntity<List<AssessmentResponse>> findPublishedByFilters(
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @Operation(summary = "Listar avaliações próximas")
    ResponseEntity<List<AssessmentResponse>> findUpcoming(
            @RequestParam(required = false, defaultValue = "30") Integer days);

    @Operation(summary = "Atualizar avaliação")
    ResponseEntity<AssessmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody AssessmentRequest request);

    @Operation(summary = "Excluir avaliação")
    ResponseEntity<Void> delete(@PathVariable Long id);

    @Operation(summary = "Publicar avaliação")
    ResponseEntity<AssessmentResponse> publish(@PathVariable Long id);

    @Operation(summary = "Despublicar avaliação")
    ResponseEntity<AssessmentResponse> unpublish(@PathVariable Long id);

}
