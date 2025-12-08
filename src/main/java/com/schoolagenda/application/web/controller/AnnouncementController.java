// src/main/java/com/schoolagenda/application/web/controller/AnnouncementController.java
package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.CreateAnnouncementRequest;
import com.schoolagenda.application.web.dto.response.AnnouncementResponse;
import com.schoolagenda.domain.model.Announcement.AnnouncementType;
import com.schoolagenda.domain.service.AnnouncementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/announcements")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PostMapping
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @Valid @RequestBody CreateAnnouncementRequest request) {
        AnnouncementResponse response = announcementService.createAnnouncement(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {
        List<AnnouncementResponse> responses = announcementService.getAllAnnouncements();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementResponse>> getActiveAnnouncements() {
        List<AnnouncementResponse> responses = announcementService.getActiveAnnouncements();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> getAnnouncementById(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.getAnnouncementById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncementsByType(
            @PathVariable AnnouncementType type) {
        List<AnnouncementResponse> responses = announcementService.getAnnouncementsByType(type);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{type}/active")
    public ResponseEntity<List<AnnouncementResponse>> getActiveAnnouncementsByType(
            @PathVariable AnnouncementType type) {
        List<AnnouncementResponse> responses = announcementService.getActiveAnnouncementsByType(type);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AnnouncementResponse>> searchAnnouncementsByTitle(
            @RequestParam String title) {
        List<AnnouncementResponse> responses = announcementService.searchAnnouncementsByTitle(title);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{type}/count")
    public ResponseEntity<Long> getAnnouncementCountByType(@PathVariable AnnouncementType type) {
        long count = announcementService.getAnnouncementCountByType(type);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/type/{type}/next-order")
    public ResponseEntity<Integer> getNextOrderPositionForType(@PathVariable AnnouncementType type) {
        Integer nextOrder = announcementService.getNextOrderPositionForType(type);
        return ResponseEntity.ok(nextOrder);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @PathVariable Long id, @Valid @RequestBody CreateAnnouncementRequest request) {
        AnnouncementResponse response = announcementService.updateAnnouncement(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<AnnouncementResponse> toggleAnnouncementStatus(@PathVariable Long id) {
        AnnouncementResponse response = announcementService.toggleAnnouncementStatus(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reorder")
    public ResponseEntity<Void> reorderAnnouncements(@RequestBody List<Long> announcementIdsInOrder) {
        announcementService.reorderAnnouncements(announcementIdsInOrder);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.noContent().build();
    }
}