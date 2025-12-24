package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.enums.NotificationType;

public class CreateNotificationRequest {
    private String username;
    private String title;
    private String message;
    private NotificationType type = NotificationType.MESSAGE;

    public CreateNotificationRequest() {}

    public CreateNotificationRequest(String username, String title, String message, NotificationType type) {
        this.username = username;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
}