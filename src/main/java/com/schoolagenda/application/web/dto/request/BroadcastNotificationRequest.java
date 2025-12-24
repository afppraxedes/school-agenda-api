package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.enums.NotificationType;

public class BroadcastNotificationRequest {
    private String title;
    private String message;
    private NotificationType type = NotificationType.ANNOUNCEMENT;

    public BroadcastNotificationRequest() {}

    public BroadcastNotificationRequest(String title, String message, NotificationType type) {
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
}
