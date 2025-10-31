package com.schoolagenda.domain.service;

import com.schoolagenda.application.web.dto.NotificationDTO;
import com.schoolagenda.domain.model.Notification;
import com.schoolagenda.domain.model.NotificationType;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.NotificationRepository;
import com.schoolagenda.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final WebPushService webPushService;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               WebPushService webPushService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.webPushService = webPushService;
    }

    public List<NotificationDTO> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NotificationDTO sendNotification(NotificationDTO notificationDTO) {
        User user = userRepository.findById(notificationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification(
                notificationDTO.getTitle(),
                notificationDTO.getMessage(),
                user,
                notificationDTO.getType()
        );

        Notification savedNotification = notificationRepository.save(notification);

        // Enviar notificação push se o usuário tiver subscription
        if (user.getPushSubscription() != null &&
                webPushService.isValidSubscription(user.getPushSubscription())) {

            webPushService.sendNotification(
                    user.getPushSubscription(),
                    notificationDTO.getTitle(),
                    notificationDTO.getMessage()
            );
        } else {
            logger.info("📨 Notification saved (no push sent) - User: {}, Title: {}", user.getUsername(), notificationDTO.getTitle());
        }

        return convertToDTO(savedNotification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

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

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
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