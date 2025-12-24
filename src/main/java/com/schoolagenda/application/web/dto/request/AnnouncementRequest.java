// src/main/java/com/schoolagenda/application/web/dto/AnnouncementRequest.java
package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.model.Announcement.AnnouncementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AnnouncementRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @NotBlank(message = "Image path is required")
    @Size(max = 255, message = "Image path must not exceed 255 characters")
    private String imagePath;

    @NotNull(message = "Type is required")
    private AnnouncementType type;

    @NotNull(message = "Order position is required")
    private Integer orderPosition = 0;

    private Boolean isActive = true;

    // Constructors
    public AnnouncementRequest() {}

    public AnnouncementRequest(String title, String description, String imagePath,
                               AnnouncementType type, Integer orderPosition, Boolean isActive) {
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.type = type;
        this.orderPosition = orderPosition;
        this.isActive = isActive;
    }

    // Getters and Setters
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
}