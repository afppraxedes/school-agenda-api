package com.schoolagenda.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolagenda.application.web.dto.request.NotificationRequest;
import com.schoolagenda.application.web.dto.response.NotificationResponse;
import com.schoolagenda.domain.model.Notification;
import com.schoolagenda.domain.enums.NotificationType;
import com.schoolagenda.domain.model.PushSubscription;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.NotificationRepository;
import com.schoolagenda.domain.repository.PushSubscriptionRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.NotificationService;
import com.schoolagenda.domain.service.WebPushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final WebPushService webPushService;
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final ObjectMapper objectMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   WebPushService webPushService, PushSubscriptionRepository pushSubscriptionRepository, ObjectMapper objectMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.webPushService = webPushService;
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        return notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


//    @Transactional(readOnly = true)
//    public List<UserResponse> getAllUsers() {
//        List<User> users = userRepository.findAll();
//        return users.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }

    // TODO: Implementação anterior que estava funcional.
//    @Override
//    public NotificationRequest sendNotification(NotificationRequest notificationRequest) {
//        User user = userRepository.findById(notificationRequest.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Notification notification = new Notification(
//                notificationRequest.getTitle(),
//                notificationRequest.getMessage(),
//                user,
//                notificationRequest.getType(),
//                notificationRequest.getUrl()
//        );
//
//        notification.setNotifiedAt(OffsetDateTime.now(ZoneOffset.UTC));
//        notification.setRead(false);
//
//        Notification savedNotification = notificationRepository.save(notification);
//
//        // Enviar notificação push se o usuário tiver subscription
//        if (user.getPushSubscription() != null &&
//                webPushService.isValidSubscription(user.getPushSubscription())) {
//
//            webPushService.sendNotification(
//                    user.getPushSubscription(),
//                    notificationRequest.getTitle(),
//                    notificationRequest.getMessage()
//            );
//        } else {
//            logger.info("📨 Notification saved (no push sent) - User: {}, Title: {}", user.getUsername(), notificationRequest.getTitle());
//        }
//
//        return convertToDTO(savedNotification);
//    }

    // NOVA IMPLEMENTAÇÃO (GEMINI)
//    @Override
//    public NotificationRequest sendNotification(NotificationRequest notificationRequest) {
//        User user = userRepository.findById(notificationRequest.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Notification notification = new Notification(
//                notificationRequest.getTitle(),
//                notificationRequest.getMessage(),
//                user,
//                notificationRequest.getType(),
//                notificationRequest.getUrl()
//        );
//
//        notification.setNotifiedAt(OffsetDateTime.now(ZoneOffset.UTC));
//
//        // Persistimos a notificação no banco primeiro
//        Notification savedNotification = notificationRepository.save(notification);
//
//        // Buscamos a subscrição ativa do usuário
//        // Nota: Como você tem a tabela push_subscriptions, o ideal é buscar por ela
//        if (user.getPushSubscription() != null &&
//                webPushService.isValidSubscription(user.getPushSubscription())) {
//
//            webPushService.sendNotification(
//                    user.getPushSubscription(),
//                    notificationRequest.getTitle(),
//                    notificationRequest.getMessage()
//            );
//
////            // Atualizamos a data de notificação efetiva
////            savedNotification.setNotifiedAt(OffsetDateTime.now(ZoneOffset.UTC));
//            notificationRepository.save(savedNotification);
//        }
//
//        return convertToDTO(savedNotification);
//    }

    @Override
    public NotificationRequest sendNotification(NotificationRequest notificationRequest) {
        User user = userRepository.findById(notificationRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification(
                notificationRequest.getTitle(),
                notificationRequest.getMessage(),
                user,
                notificationRequest.getType(),
                notificationRequest.getUrl()
        );

        // 1. Persistimos a notificação no banco (estado: criada)
        Notification savedNotification = notificationRepository.save(notification);

        // 2. BUSCA NA TABELA CORRETA: Pegamos todos os endpoints deste usuário
        List<com.schoolagenda.domain.model.PushSubscription> subscriptions =
                pushSubscriptionRepository.findByUserId(user.getId());

        if (!subscriptions.isEmpty()) {
            // 3. Loop para enviar para todos os dispositivos (celular, PC, etc)
            subscriptions.forEach(sub -> {
                try {
                    // Aqui passamos os dados da tabela, convertidos para o JSON que o WebPushService espera
                    // Ou ajustamos o WebPushService para receber o objeto PushSubscription diretamente
                    webPushService.sendNotification(
                            convertSubscriptionToJson(sub),
                            notificationRequest.getTitle(),
                            notificationRequest.getMessage()
                    );
                } catch (Exception e) {
                    logger.error("❌ Falha ao enviar para o dispositivo {}: {}", sub.getId(), e.getMessage());
                }
            });

            // 4. Atualizamos a data de notificação efetiva apenas se tentamos enviar
            savedNotification.setNotifiedAt(OffsetDateTime.now(ZoneOffset.UTC));
            notificationRepository.save(savedNotification);
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

    // TODO: Implementação anterior que estava funcional.
//    @Override
//    public void broadcastNotification(String title, String message, NotificationType type, String url) {
//        List<User> users = userRepository.findAll();
//        int pushSent = 0;
//
//        for (User user : users) {
//            Notification notification = new Notification(title, message, user, type, url);
//            notificationRepository.save(notification);
//
//            // Enviar push notification se o usuário tiver subscription
//            if (user.getPushSubscription() != null &&
//                    webPushService.isValidSubscription(user.getPushSubscription())) {
//
//                webPushService.sendNotification(
//                        user.getPushSubscription(),
//                        title,
//                        message
//                );
//                pushSent++;
//            }
//        }
//
//        logger.info("📢 Broadcast completed - Notifications: {}, Push sent: {}", users.size(), pushSent);
//    }

    // TODO: Nova implementação (GEMINI) - Broadcast para múltiplos dispositivos por usuário
    @Override
    @Transactional
    public void broadcastNotification(String title, String message, NotificationType type, String url) {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            Notification notification = new Notification(title, message, user, type, url);
            notification.setRead(false);
            // Importante: Definir aqui ou garantir que o save retorne a instância atualizada
            notification.setNotifiedAt(OffsetDateTime.now(ZoneOffset.UTC));

            notificationRepository.save(notification);

            List<PushSubscription> subs = pushSubscriptionRepository.findByUserId(user.getId());
            subs.forEach(sub -> {
                webPushService.sendNotification(convertSubscriptionToJson(sub), title, message);
            });
        }
    }

    @Override
    @Transactional
    public void broadcastToClass(String title, String message, String className, NotificationType type, String url) {
        List<User> targets = userRepository.findResponsiblesByClassName(className);

        logger.info("📢 Iniciando broadcast para turma {}: {} usuários encontrados", className, targets.size());

        for (User user : targets) {
            // 1. Criar registro de notificação
            Notification notification = new Notification(title, message, user, type, url);
            notificationRepository.save(notification);

            // 2. Buscar subscrições na tabela push_subscriptions
            List<PushSubscription> subs = pushSubscriptionRepository.findByUserId(user.getId());

            // 3. Enviar para todos os dispositivos
            subs.forEach(sub -> {
                webPushService.sendNotification(convertSubscriptionToJson(sub), title, message);
            });

            // 4. Marcar notifiedAt
            notification.setNotifiedAt(OffsetDateTime.now(ZoneOffset.UTC));
            notificationRepository.save(notification);
        }
    }

    // Método auxiliar para evitar duplicação de código
//    private void processSingleNotification(User user, String title, String message, NotificationType type, String url) {
//        Notification notification = new Notification(title, message, user, type, url);
//        notificationRepository.save(notification);
//
//        List<PushSubscription> subs = pushSubscriptionRepository.findByUserId(user.getId());
//        subs.forEach(sub -> webPushService.sendNotification(convertSubscriptionToJson(sub), title, message));
//    }

    private String convertSubscriptionToJson(com.schoolagenda.domain.model.PushSubscription sub) {
        try {
            // Criamos um mapa ou um DTO temporário para bater com a estrutura do PushSubscriptionRequest
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("endpoint", sub.getEndpoint());
            jsonMap.put("expirationTime", sub.getExpirationTime());

            Map<String, String> keys = new HashMap<>();
            keys.put("p256dh", sub.getP256dh());
            keys.put("auth", sub.getAuth());

            jsonMap.put("keys", keys);

            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            logger.error("❌ Erro ao converter subscrição para JSON: {}", e.getMessage());
            return null;
        }
    }

    // TODO: Colocar conforme o "UserReponseImpl":
    //  private UserResponse convertToResponse(User user) {
    //        String profileType = determineProfileType(user.getRoles());
    //        return UserResponse.builder()
    //                .id(user.getId())
    //                .email(user.getEmail())
    //                .username(user.getUsername())
    //                .name(user.getName())
    //                .roles(user.getRoles())
    //                .pushSubscription(user.getPushSubscription())
    //                .profileType(profileType)
    //                .build();
    //    }

    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .icon(notification.getIcon())
                .url(notification.getUrl())
                .userId(notification.getUser().getId())
                .userName(notification.getUser().getName())
                .read(notification.isRead())
                .notifiedAt(notification.getNotifiedAt())
                .type(notification.getType())
                .userRole(notification.getUser().getRoles().toString())
                .build();
    }

    private NotificationRequest convertToDTO(Notification notification) {
        NotificationRequest dto = new NotificationRequest();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setIcon(notification.getIcon());
        dto.setUrl(notification.getUrl());
        dto.setUserId(notification.getUser().getId());
        dto.setUserName(notification.getUser().getName());
        dto.setRead(notification.isRead());
        dto.setNotifiedAt(notification.getNotifiedAt());
        dto.setType(notification.getType());
        dto.setUserRole(notification.getUser().getRoles().stream()
                .findFirst()
                .map(Enum::name)
                .orElse(""));
        return dto;
    }
}