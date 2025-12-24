// src/main/java/com/schoolagenda/domain/model/Announcement.java
package com.schoolagenda.domain.model;

import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_path", nullable = false, length = 255)
    private String imagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AnnouncementType type;

    @Column(name = "order_position", nullable = false)
    private Integer orderPosition = 0;

//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // Enum para os tipos de anúncio
    public enum AnnouncementType {
        CAROUSEL, BANNER
    }

    // Constructors
    public Announcement() {
//        this.createdAt = LocalDateTime.now();
//        this.updatedAt = LocalDateTime.now();
        this.orderPosition = 0;
        this.isActive = true;
    }

    public Announcement(String title, String description, String imagePath,
                        AnnouncementType type, Integer orderPosition) {
        this();
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.type = type;
        this.orderPosition = orderPosition;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
//        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
//        this.updatedAt = LocalDateTime.now();
    }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
//        this.updatedAt = LocalDateTime.now();
    }

    public AnnouncementType getType() { return type; }
    public void setType(AnnouncementType type) {
        this.type = type;
//        this.updatedAt = LocalDateTime.now();
    }

    public Integer getOrderPosition() { return orderPosition; }
    public void setOrderPosition(Integer orderPosition) {
        this.orderPosition = orderPosition;
//        this.updatedAt = LocalDateTime.now();
    }

//    public LocalDateTime getCreatedAt() { return createdAt; }
//    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
//
//    public LocalDateTime getUpdatedAt() { return updatedAt; }
//    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
//        this.updatedAt = LocalDateTime.now();
    }

    // Utility methods
//    @PreUpdate
//    public void preUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }
}