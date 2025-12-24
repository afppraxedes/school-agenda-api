package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.SchoolClassRequest;
import com.schoolagenda.application.web.dto.response.SchoolClassResponse;
import com.schoolagenda.domain.model.SchoolClass;
import com.schoolagenda.domain.service.SchoolClassService;
import jakarta.validation.Valid;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/school-classes")
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    public SchoolClassController(SchoolClassService schoolClassService) {
        this.schoolClassService = schoolClassService;
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'DIRECTOR')")
    @PostMapping
    public ResponseEntity<SchoolClassResponse> createClass(
            @Valid @RequestBody SchoolClassRequest schoolClassRequest) {
        SchoolClassResponse response = schoolClassService.create(schoolClassRequest);
        return ResponseEntity.status(HttpStatus.SC_CREATED).body(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'DIRECTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<SchoolClassResponse> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody SchoolClassRequest schoolClassRequest) {
        SchoolClassResponse response = schoolClassService.update(id, schoolClassRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'DIRECTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long id) {
        schoolClassService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'DIRECTOR', 'TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<SchoolClassResponse> findById(@PathVariable Long id) {
        SchoolClassResponse response = schoolClassService.findById(id);
        return ResponseEntity.ok(response);
    }
    // TODO: Apenas para testes para ver se os campos de auditoria aparecerão no "response"
    // Verificar quais são os "perfis" que acessarão os endpoints de "consultas"!
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'DIRECTOR', 'TEACHER')")
    @GetMapping
    public ResponseEntity<List<SchoolClassResponse>> findAll() {
        List<SchoolClassResponse> responses = schoolClassService.findAll();
        return ResponseEntity.ok(responses);
    }
}
