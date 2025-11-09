package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.NotificationRequest;
import com.schoolagenda.application.web.dto.request.PushSubscriptionRequest;
import com.schoolagenda.domain.model.NotificationType;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.impl.NotificationServiceImpl;
import com.schoolagenda.domain.service.impl.UserServiceImpl;
import com.schoolagenda.domain.service.WebPushService;
import infrastructure.external.webpush.WebPushServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200/**")
public class NotificationController {
    private final NotificationServiceImpl notificationServiceImpl;
    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;
    private final WebPushService webPushService;

    public NotificationController(NotificationServiceImpl notificationServiceImpl,
                                  UserServiceImpl userServiceImpl,
                                  UserRepository userRepository,
                                  WebPushService webPushService) {
        this.notificationServiceImpl = notificationServiceImpl;
        this.userServiceImpl = userServiceImpl;
        this.userRepository = userRepository;
        this.webPushService = webPushService;
    }

    // Endpoint para obter a chave pública VAPID
    @GetMapping("/vapid-public-key")
    public ResponseEntity<Map<String, String>> getVapidPublicKey() {
        try {
            String publicKey = webPushService.getVapidPublicKey();
            Map<String, String> response = new HashMap<>();
            response.put("publicKey", publicKey);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<NotificationRequest>> getUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userServiceImpl.findByUsername(username)
                .map(user -> {
                    List<NotificationRequest> notifications = notificationServiceImpl.getUserNotifications(user.getId());
                    return ResponseEntity.ok(notifications);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribeToPush(@RequestBody PushSubscriptionRequest subscription) {
        // TODO: este era o método que estava salvando as "PUSH_SUBSCRIPTIONS"!
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//
//        return userService.findByUsername(username)
//                .map(user -> {
//                    userService.savePushSubscription(user.getId(), subscription.getSubscription());
//                    return ResponseEntity.ok().<Void>build();
//                })
//                .orElse(ResponseEntity.notFound().build());

        return null;
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationRequest> sendNotification(@RequestBody NotificationRequest notificationRequest) {
        NotificationRequest sentNotification = notificationServiceImpl.sendNotification(notificationRequest);
        return ResponseEntity.ok(sentNotification);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationServiceImpl.broadcastNotification(
                notificationRequest.getTitle(),
                notificationRequest.getMessage(),
                notificationRequest.getType()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<NotificationRequest> createNotification(@RequestBody CreateNotificationRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + request.getUsername()));

            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setTitle(request.getTitle());
            notificationRequest.setMessage(request.getMessage());
            notificationRequest.setUserId(user.getId());
            notificationRequest.setType(request.getType());

            NotificationRequest sentNotification = notificationServiceImpl.sendNotification(notificationRequest);
            return ResponseEntity.ok(sentNotification);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/broadcast-to-all")
    public ResponseEntity<Void> broadcastToAll(@RequestBody BroadcastNotificationRequest request) {
        try {
            notificationServiceImpl.broadcastNotification(
                    request.getTitle(),
                    request.getMessage(),
                    request.getType()
            );
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationServiceImpl.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userServiceImpl.findByUsername(username)
                .map(user -> {
                    Long count = notificationServiceImpl.getUnreadCount(user.getId());
                    return ResponseEntity.ok(count);
                })
                .orElse(ResponseEntity.ok(0L));
    }

    // Adicionar este método ao NotificationController
    @GetMapping("/push-status")
    public ResponseEntity<Map<String, Object>> getPushStatus() {
        try {
            Map<String, Object> status = new HashMap<>();

            // Verificar se o WebPushService é a implementação real
            if (webPushService instanceof WebPushServiceImpl) {
                WebPushServiceImpl impl =
                        (WebPushServiceImpl) webPushService;
                status.put("pushEnabled", impl.isPushEnabled());
            } else {
                status.put("pushEnabled", false);
            }

            status.put("vapidPublicKey", webPushService.getVapidPublicKey().substring(0, 20) + "...");
            status.put("service", "WebPushService");

            return ResponseEntity.ok(status);

        } catch (Exception e) {
            Map<String, Object> errorStatus = new HashMap<>();
            errorStatus.put("pushEnabled", false);
            errorStatus.put("error", e.getMessage());
            return ResponseEntity.ok(errorStatus);
        }
    }

    // Classes DTO internas para requests simplificadas
    public static class CreateNotificationRequest {
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

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public NotificationType getType() { return type; }
        public void setType(NotificationType type) { this.type = type; }
    }

    public static class BroadcastNotificationRequest {
        private String title;
        private String message;
        private NotificationType type = NotificationType.ANNOUNCEMENT;

        public BroadcastNotificationRequest() {}

        public BroadcastNotificationRequest(String title, String message, NotificationType type) {
            this.title = title;
            this.message = message;
            this.type = type;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public NotificationType getType() { return type; }
        public void setType(NotificationType type) { this.type = type; }
    }
}