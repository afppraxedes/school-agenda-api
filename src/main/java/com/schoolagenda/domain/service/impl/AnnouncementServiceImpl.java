// src/main/java/com/schoolagenda/domain/service/AnnouncementServiceImpl.java
package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.AnnouncementRequest;
import com.schoolagenda.application.web.dto.response.AnnouncementResponse;
import com.schoolagenda.application.web.dto.response.SchoolClassResponse;
import com.schoolagenda.application.web.mapper.AnnouncementMapper;
import com.schoolagenda.domain.model.Announcement;
import com.schoolagenda.domain.model.Announcement.AnnouncementType;
import com.schoolagenda.domain.repository.AnnouncementRepository;
import com.schoolagenda.domain.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementMapper announcementMapper;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository, AnnouncementMapper announcementMapper) {
        this.announcementRepository = announcementRepository;
        this.announcementMapper = announcementMapper;
    }

    @Override
    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        // Check if title already exists
        if (announcementRepository.existsByTitle(request.getTitle())) {
            throw new RuntimeException("Announcement with title '" + request.getTitle() + "' already exists");
        }

        // TODO: REVER ESTE MÉTODO, POIS AGORA ESTOU UTILIZANDO O "MAP STRUCT"!


        // If order position is not provided, get the next available
        Integer orderPosition = request.getOrderPosition();
        if (orderPosition == null) {
            orderPosition = getNextOrderPositionForType(request.getType());
        }

        Announcement announcement = new Announcement(
                request.getTitle(),
                request.getDescription(),
                request.getImagePath(),
                request.getType(),
                orderPosition
        );

        announcement.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        Announcement savedAnnouncement = announcementRepository.save(announcement);
        return convertToResponse(savedAnnouncement);
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));

        return convertToResponse(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAllAnnouncements() {
        return announcementRepository.findAll().stream()
                .map(announcementMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getActiveAnnouncements() {
        List<Announcement> announcements = announcementRepository.findByIsActiveTrueOrderByOrderPositionAsc();

        return announcements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsByType(AnnouncementType type) {
        List<Announcement> announcements = announcementRepository.findByTypeOrderByOrderPositionAsc(type);

        return announcements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getActiveAnnouncementsByType(AnnouncementType type) {
        List<Announcement> announcements = announcementRepository.findByTypeAndIsActiveTrueOrderByOrderPositionAsc(type);

        return announcements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> searchAnnouncementsByTitle(String title) {
        List<Announcement> announcements = announcementRepository.findByTitleContainingIgnoreCase(title);

        return announcements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AnnouncementResponse updateAnnouncement(Long id, AnnouncementRequest request) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));

        // Check if title is being changed and if new title already exists
        if (!announcement.getTitle().equals(request.getTitle()) &&
                announcementRepository.existsByTitle(request.getTitle())) {
            throw new RuntimeException("Announcement with title '" + request.getTitle() + "' already exists");
        }

        announcement.setTitle(request.getTitle());
        announcement.setDescription(request.getDescription());
        announcement.setImagePath(request.getImagePath());
        announcement.setType(request.getType());
        announcement.setOrderPosition(request.getOrderPosition());

        if (request.getIsActive() != null) {
            announcement.setIsActive(request.getIsActive());
        }

        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        return convertToResponse(updatedAnnouncement);
    }

    @Override
    @Transactional
    public AnnouncementResponse toggleAnnouncementStatus(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + id));

        announcement.setIsActive(!announcement.getIsActive());

        Announcement updatedAnnouncement = announcementRepository.save(announcement);
        return convertToResponse(updatedAnnouncement);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new RuntimeException("Announcement not found with id: " + id);
        }
        announcementRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void reorderAnnouncements(List<Long> announcementIdsInOrder) {
        for (int i = 0; i < announcementIdsInOrder.size(); i++) {
            Long announcementId = announcementIdsInOrder.get(i);
            Announcement announcement = announcementRepository.findById(announcementId)
                    .orElseThrow(() -> new RuntimeException("Announcement not found with id: " + announcementId));

            announcement.setOrderPosition(i);
            announcementRepository.save(announcement);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getAnnouncementCountByType(AnnouncementType type) {
        return announcementRepository.countByTypeAndIsActiveTrue(type);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getNextOrderPositionForType(AnnouncementType type) {
        return announcementRepository.findMaxOrderPositionByType(type)
                .orElse(-1) + 1;
    }

    /**
     * Converts Announcement entity to Response DTO
     */
    private AnnouncementResponse convertToResponse(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getDescription(),
                announcement.getImagePath(),
                announcement.getType(),
                announcement.getOrderPosition(),
                announcement.getIsActive()
//                announcement.getCreatedBy(),
//                announcement.getLastModifiedBy(),
//                announcement.getCreatedAt(),
//                announcement.getUpdatedAt()
        );
    }
}