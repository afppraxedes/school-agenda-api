package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.SchoolEventRequest;
import com.schoolagenda.application.web.dto.response.SchoolEventResponse;
import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
import com.schoolagenda.domain.service.SchoolEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/school-events")
//@RequiredArgsConstructor
public class SchoolEventController {

    private final SchoolEventService eventService;

    public SchoolEventController(SchoolEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SchoolEventResponse>> getCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime end,
            @AuthenticationPrincipal AgendaUserDetails currentUser) {
        return ResponseEntity.ok(eventService.getCalendar(start, end, currentUser));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    public ResponseEntity<SchoolEventResponse> create(@Valid @RequestBody SchoolEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    public ResponseEntity<SchoolEventResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SchoolEventRequest request) {
        return ResponseEntity.ok(eventService.update(id, request));
    }
}
