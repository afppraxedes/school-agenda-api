package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.response.TeacherDashboardResponse;
import com.schoolagenda.domain.service.TeacherDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/dashboard/teacher")
public class TeacherDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(TeacherDashboardController.class);
    private final TeacherDashboardService dashboardService;

    public TeacherDashboardController(TeacherDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TEACHER')")
    public ResponseEntity<TeacherDashboardResponse> getDashboard(Authentication authentication) {
        logger.info("📊 Gerando dashboard para o professor: {}", authentication.getName());
        // authentication.getName() retorna o 'sub' do seu JWT (email)
        return ResponseEntity.ok(dashboardService.getTeacherDashboardData(authentication.getName()));
    }
}