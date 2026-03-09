package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.response.ResponsibleDashboardResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.service.ResponsibleDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard/responsible")
@RequiredArgsConstructor
public class ResponsibleDashboardController {

    private final ResponsibleDashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('RESPONSIBLE')")
    public ResponseEntity<ResponsibleDashboardResponse> getDashboardData(
            @AuthenticationPrincipal AgendaUserDetails currentUser) {

        // Passamos o ID do usuário logado (ex: ID 22)
        return ResponseEntity.ok(dashboardService.getResponsibleDashboard(currentUser.getId()));
    }
}