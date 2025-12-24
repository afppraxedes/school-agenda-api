// src/main/java/com/schoolagenda/application/web/controller/AnnouncementController.java
package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.AnnouncementRequest;
import com.schoolagenda.application.web.dto.response.AnnouncementResponse;
import com.schoolagenda.domain.model.Announcement.AnnouncementType;
import com.schoolagenda.domain.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    // TODO: "RBCA" APENAS PARA TESTES, POIS SERÁ REVISADO PELO GEMINI!

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @PostMapping
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @Valid @RequestBody AnnouncementRequest request) {
        AnnouncementResponse response = announcementService.createAnnouncement(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {
        List<AnnouncementResponse> responses = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementResponse>> getActiveAnnouncements() {
        List<AnnouncementResponse> responses = announcementService.getActiveAnnouncements();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> getAnnouncementById(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.getAnnouncementById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByType(
            @PathVariable AnnouncementType type) {
        List<AnnouncementResponse> responses = announcementService.getAnnouncementsByType(type);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @GetMapping("/type/{type}/active")
    public ResponseEntity<List<AnnouncementResponse>> getActiveAnnouncementsByType(
            @PathVariable AnnouncementType type) {
        List<AnnouncementResponse> responses = announcementService.getActiveAnnouncementsByType(type);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @GetMapping("/search")
    public ResponseEntity<List<AnnouncementResponse>> searchAnnouncementsByTitle(
            @RequestParam String title) {
        List<AnnouncementResponse> responses = announcementService.searchAnnouncementsByTitle(title);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @GetMapping("/type/{type}/count")
    public ResponseEntity<Long> getAnnouncementCountByType(@PathVariable AnnouncementType type) {
        long count = announcementService.getAnnouncementCountByType(type);
        return ResponseEntity.ok(count);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @GetMapping("/type/{type}/next-order")
    public ResponseEntity<Integer> getNextOrderPositionForType(@PathVariable AnnouncementType type) {
        Integer nextOrder = announcementService.getNextOrderPositionForType(type);
        return ResponseEntity.ok(nextOrder);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @PathVariable Long id, @Valid @RequestBody AnnouncementRequest request) {
        AnnouncementResponse response = announcementService.updateAnnouncement(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<AnnouncementResponse> toggleAnnouncementStatus(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.toggleAnnouncementStatus(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @PostMapping("/reorder")
    public ResponseEntity<Void> reorderAnnouncements(@RequestBody List<Long> announcementIdsInOrder) {
        announcementService.reorderAnnouncements(announcementIdsInOrder);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ADMINISTRATOR', 'DIRECTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}