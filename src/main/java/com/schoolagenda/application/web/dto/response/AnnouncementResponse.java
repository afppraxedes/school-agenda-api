// src/main/java/com/schoolagenda/application/web/dto/AnnouncementResponse.java
package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.domain.model.Announcement.AnnouncementType;
import java.time.LocalDateTime;

public class AnnouncementResponse {

    private Long id;
    private String title;
    private String description;
    private String imagePath;
    private AnnouncementType type;
    private Integer orderPosition;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AnnouncementResponse() {}

    public AnnouncementResponse(Long id, String title, String description, String imagePath,
                                AnnouncementType type, Integer orderPosition, Boolean isActive,
                                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.type = type;
        this.orderPosition = orderPosition;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public AnnouncementType getType() { return type; }
    public void setType(AnnouncementType type) { this.type = type; }

    public Integer getOrderPosition() { return orderPosition; }
    public void setOrderPosition(Integer orderPosition) { this.orderPosition = orderPosition; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}