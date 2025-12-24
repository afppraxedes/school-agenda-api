package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.request.NotificationRequest;
import com.schoolagenda.application.web.dto.response.NotificationResponse;
import com.schoolagenda.domain.enums.NotificationType;

import java.util.List;

public interface NotificationService {
//    List<NotificationRequest> getUserNotifications(Long userId);
    List<NotificationResponse> getUserNotifications(Long userId);
    List<NotificationResponse> getAllNotifications();
    NotificationRequest sendNotification(NotificationRequest notificationRequest);
    void markAsRead(Long notificationId);
    Long getUnreadCount(Long userId);
    public void broadcastNotification(String title, String message, NotificationType type);
}
