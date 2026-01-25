package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.response.StudentDashboardResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard") // Aqui definimos o caminho que o Angular vai chamar
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints para o painel principal")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/student")
    @PreAuthorize("hasAuthority('STUDENT')") // Garante que só alunos acessem
    public ResponseEntity<StudentDashboardResponse> getStudentDashboard(
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        return ResponseEntity.ok(dashboardService.getStudentDashboard(currentUser.getId()));
    }
}
