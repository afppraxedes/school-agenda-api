package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.NotificationRequest;
import com.schoolagenda.domain.model.Notification;
import com.schoolagenda.domain.model.NotificationType;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.NotificationRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.NotificationService;
import com.schoolagenda.domain.service.WebPushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final WebPushService webPushService;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   WebPushService webPushService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.webPushService = webPushService;
    }

    @Override
    public List<NotificationRequest> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationRequest sendNotification(NotificationRequest notificationRequest) {
        User user = userRepository.findById(notificationRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification(
                notificationRequest.getTitle(),
                notificationRequest.getMessage(),
                user,
                notificationRequest.getType()
        );

        Notification savedNotification = notificationRepository.save(notification);

        // Enviar notificação push se o usuário tiver subscription
        if (user.getPushSubscription() != null &&
                webPushService.isValidSubscription(user.getPushSubscription())) {

            webPushService.sendNotification(
                    user.getPushSubscription(),
                    notificationRequest.getTitle(),
                    notificationRequest.getMessage()
            );
        } else {
            logger.info("📨 Notification saved (no push sent) - User: {}, Title: {}", user.getUsername(), notificationRequest.getTitle());
        }

        return convertToDTO(savedNotification);
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    public void broadcastNotification(String title, String message, NotificationType type) {
        List<User> users = userRepository.findAll();
        int pushSent = 0;

        for (User user : users) {
            Notification notification = new Notification(title, message, user, type);
            notificationRepository.save(notification);

            // Enviar push notification se o usuário tiver subscription
            if (user.getPushSubscription() != null &&
                    webPushService.isValidSubscription(user.getPushSubscription())) {

                webPushService.sendNotification(
                        user.getPushSubscription(),
                        title,
                        message
                );
                pushSent++;
            }
        }

        logger.info("📢 Broadcast completed - Notifications: {}, Push sent: {}", users.size(), pushSent);
    }

    private NotificationRequest convertToDTO(Notification notification) {
        NotificationRequest dto = new NotificationRequest();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setUserId(notification.getUser().getId());
        dto.setUserName(notification.getUser().getName());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setType(notification.getType());
        dto.setUserRole(notification.getUser().getRoles().stream()
                .findFirst()
                .map(Enum::name)
                .orElse(""));
        return dto;
    }
}