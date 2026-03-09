package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.enums.NotificationType;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class NotificationRequest {
    private Long id;
    private String title;
    private String message;
    private String url;
    private String icon;
    private Long userId;
    private String userName;
    private boolean read;
    private OffsetDateTime notifiedAt;
    private NotificationType type;
    private String userRole;

    public NotificationRequest() {}

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public OffsetDateTime getNotifiedAt() {
        return notifiedAt;
    }

    public void setNotifiedAt(OffsetDateTime notifiedAt) {
        this.notifiedAt = notifiedAt;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}