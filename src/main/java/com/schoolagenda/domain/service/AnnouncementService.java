// src/main/java/com/schoolagenda/domain/service/AnnouncementService.java
package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.AnnouncementRequest;
import com.schoolagenda.application.web.dto.response.AnnouncementResponse;
import com.schoolagenda.domain.model.Announcement.AnnouncementType;

import java.util.List;

public interface AnnouncementService {

    // Create a new announcement
    AnnouncementResponse createAnnouncement(AnnouncementRequest request);

    // Get announcement by ID
    AnnouncementResponse getAnnouncementById(Long id);

    // Get all announcements
    List<AnnouncementResponse> getAllAnnouncements();

    // Get active announcements
    List<AnnouncementResponse> getActiveAnnouncements();

    // Get announcements by type
    List<AnnouncementResponse> getAnnouncementsByType(AnnouncementType type);

    // Get active announcements by type
    List<AnnouncementResponse> getActiveAnnouncementsByType(AnnouncementType type);

    // Search announcements by title
    List<AnnouncementResponse> searchAnnouncementsByTitle(String title);

    // Update announcement
    AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request);

    // Toggle announcement active status
    AnnouncementResponse toggleAnnouncementStatus(Long id);

    // Delete announcement
    void deleteAnnouncement(Long id);

    // Reorder announcements
    void reorderAnnouncements(List<Long> announcementIdsInOrder);

    // Get announcement count by type
    long getAnnouncementCountByType(AnnouncementType type);

    // Get next order position for type
    Integer getNextOrderPositionForType(AnnouncementType type);
}