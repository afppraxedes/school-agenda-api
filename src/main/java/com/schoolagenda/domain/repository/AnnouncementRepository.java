// src/main/java/com/schoolagenda/domain/repository/AnnouncementRepository.java
package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.Announcement;
import com.schoolagenda.domain.model.Announcement.AnnouncementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    // Find all active announcements
    List<Announcement> findByIsActiveTrue();

    // Find announcements by type
    List<Announcement> findByType(AnnouncementType type);

    // Find active announcements by type
    List<Announcement> findByTypeAndIsActiveTrue(AnnouncementType type);

    // Find announcements by title containing (search)
    List<Announcement> findByTitleContainingIgnoreCase(String title);

    // Find active announcements ordered by order position
    List<Announcement> findByIsActiveTrueOrderByOrderPositionAsc();

    // Find announcements by type ordered by order position
    List<Announcement> findByTypeOrderByOrderPositionAsc(AnnouncementType type);

    // Find active announcements by type ordered by order position
    List<Announcement> findByTypeAndIsActiveTrueOrderByOrderPositionAsc(AnnouncementType type);

    // Find maximum order position for a type
    @Query("SELECT MAX(a.orderPosition) FROM Announcement a WHERE a.type = :type")
    Optional<Integer> findMaxOrderPositionByType(@Param("type") AnnouncementType type);

    // Count active announcements by type
    long countByTypeAndIsActiveTrue(AnnouncementType type);

    // Check if title exists (for unique validation)
    boolean existsByTitle(String title);

    // Find by title (exact match)
    Optional<Announcement> findByTitle(String title);
}