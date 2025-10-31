package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.NotificationDTO;
import com.schoolagenda.application.web.dto.PushSubscriptionDTO;
import com.schoolagenda.domain.model.NotificationType;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.NotificationService;
import com.schoolagenda.domain.service.UserService;
import com.schoolagenda.domain.service.WebPushService;
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
    private final NotificationService notificationService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final WebPushService webPushService;

    public NotificationController(NotificationService notificationService,
                                  UserService userService,
                                  UserRepository userRepository,
                                  WebPushService webPushService) {
        this.notificationService = notificationService;
        this.userService = userService;
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
    public ResponseEntity<List<NotificationDTO>> getUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userService.findByUsername(username)
                .map(user -> {
                    List<NotificationDTO> notifications = notificationService.getUserNotifications(user.getId());
                    return ResponseEntity.ok(notifications);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Void> subscribeToPush(@RequestBody PushSubscriptionDTO subscription) {
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
    public ResponseEntity<NotificationDTO> sendNotification(@RequestBody NotificationDTO notificationDTO) {
        NotificationDTO sentNotification = notificationService.sendNotification(notificationDTO);
        return ResponseEntity.ok(sentNotification);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastNotification(@RequestBody NotificationDTO notificationDTO) {
        notificationService.broadcastNotification(
                notificationDTO.getTitle(),
                notificationDTO.getMessage(),
                notificationDTO.getType()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody CreateNotificationRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + request.getUsername()));

            NotificationDTO notificationDTO = new NotificationDTO();
            notificationDTO.setTitle(request.getTitle());
            notificationDTO.setMessage(request.getMessage());
            notificationDTO.setUserId(user.getId());
            notificationDTO.setType(request.getType());

            NotificationDTO sentNotification = notificationService.sendNotification(notificationDTO);
            return ResponseEntity.ok(sentNotification);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/broadcast-to-all")
    public ResponseEntity<Void> broadcastToAll(@RequestBody BroadcastNotificationRequest request) {
        try {
            notificationService.broadcastNotification(
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
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userService.findByUsername(username)
                .map(user -> {
                    Long count = notificationService.getUnreadCount(user.getId());
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
            if (webPushService instanceof com.schoolagenda.application.infrastructure.webpush.WebPushServiceImpl) {
                com.schoolagenda.application.infrastructure.webpush.WebPushServiceImpl impl =
                        (com.schoolagenda.application.infrastructure.webpush.WebPushServiceImpl) webPushService;
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