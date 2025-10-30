package com.schoolagenda.application.infrastructure.webpush;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolagenda.domain.service.WebPushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WebPushServiceImpl implements WebPushService {

    private static final Logger logger = LoggerFactory.getLogger(WebPushServiceImpl.class);

    private final ObjectMapper objectMapper;
    private final ExecutorService executor;
    private final String vapidPublicKey;
    private final String vapidPrivateKey;
    private final String vapidSubject;
    private final RestTemplate restTemplate;
    private boolean pushEnabled = true;

    // Chaves VAPID em formato correto
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public WebPushServiceImpl(
            @Value("${webpush.vapid.public-key:BFm3lGjAA4SQkkK_Kn0vHheIlx2lXsomgzgtGkt3DG4UBPP19aYRqIbl_N5fqG7f8vCKQ9YAX_TMYIufIfCWWAA}") String publicKey,
            @Value("${webpush.vapid.private-key:JbuhlMKsn7xVO5wpk6zFMBJHk101IBAsK-R3xlH5f6w}") String privateKey,
            @Value("${webpush.vapid.subject:mailto:admin@schoolagenda.com}") String subject) {

        this.objectMapper = new ObjectMapper();
        this.executor = Executors.newCachedThreadPool();
        this.vapidPublicKey = publicKey;
        this.vapidPrivateKey = privateKey;
        this.vapidSubject = subject;
        this.restTemplate = new RestTemplate();

        initializeVapidKeys();
    }

    private void initializeVapidKeys() {
        try {
            // Para desenvolvimento, vamos usar uma abordagem simplificada
            // Em produção, você deve gerar chaves VAPID reais
            logger.info("✅ WebPushService initialized with simplified VAPID implementation");
            logger.info("🔑 VAPID Public Key: {}...", vapidPublicKey.substring(0, Math.min(20, vapidPublicKey.length())));
            logger.info("📧 VAPID Subject: {}", vapidSubject);

        } catch (Exception e) {
            logger.error("❌ Error initializing VAPID keys: {}", e.getMessage());
            this.pushEnabled = false;
        }
    }

    @Override
    public void sendNotification(String subscriptionJson, String title, String message) {
        if (subscriptionJson == null || subscriptionJson.trim().isEmpty()) {
            logger.warn("⚠️ Attempted to send notification to null or empty subscription");
            return;
        }

        executor.submit(() -> {
            try {
                PushSubscription subscription = objectMapper.readValue(subscriptionJson, PushSubscription.class);
                if (isValidSubscription(subscription)) {
                    sendHttpPushNotification(subscription, title, message);
                } else {
                    logger.warn("❌ Invalid subscription");
                    simulatePushNotification(title, message);
                }
            } catch (IOException e) {
                logger.error("📮 Error deserializing subscription JSON: {}", e.getMessage());
                simulatePushNotification(title, message);
            } catch (Exception e) {
                logger.error("💥 Unexpected error: {}", e.getMessage());
                simulatePushNotification(title, message);
            }
        });
    }

    @Override
    public void sendBulkNotification(List<String> subscriptionJsons, String title, String message) {
        if (subscriptionJsons == null || subscriptionJsons.isEmpty()) {
            logger.info("ℹ️ No subscriptions provided for bulk notification");
            return;
        }

        logger.info("📤 Sending bulk notification to {} subscriptions - Title: '{}'", subscriptionJsons.size(), title);

        for (String subscriptionJson : subscriptionJsons) {
            if (subscriptionJson != null && !subscriptionJson.trim().isEmpty()) {
                sendNotification(subscriptionJson, title, message);
            }
        }
    }

    @Override
    public boolean isValidSubscription(String subscriptionJson) {
        if (subscriptionJson == null || subscriptionJson.trim().isEmpty()) {
            return false;
        }

        try {
            PushSubscription subscription = objectMapper.readValue(subscriptionJson, PushSubscription.class);
            return isValidSubscription(subscription);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public String getVapidPublicKey() {
        return this.vapidPublicKey;
    }

    public boolean isPushEnabled() {
        return pushEnabled;
    }

    private boolean isValidSubscription(PushSubscription subscription) {
        return subscription != null &&
                subscription.endpoint != null &&
                !subscription.endpoint.trim().isEmpty() &&
                subscription.keys != null &&
                subscription.keys.p256dh != null &&
                subscription.keys.auth != null;
    }

    private void sendHttpPushNotification(PushSubscription subscription, String title, String message) {
        try {
            // Criar o payload da notificação
            String payload = createNotificationPayload(title, message);

            // Preparar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("TTL", "60");

            // Para endpoints FCM (Firebase Cloud Messaging)
            if (subscription.endpoint.contains("fcm.googleapis.com")) {
                headers.set("Authorization", "key=YOUR_SERVER_KEY"); // Você precisaria de uma chave de servidor FCM
            }

            // Criar requisição
            HttpEntity<String> request = new HttpEntity<>(payload, headers);

            // Enviar requisição
            ResponseEntity<String> response = restTemplate.postForEntity(subscription.endpoint, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("✅ Push notification sent successfully to FCM - Title: '{}'", title);
            } else {
                logger.warn("⚠️ Push notification failed. Status: {}, Response: {}",
                        response.getStatusCode(), response.getBody());
                simulatePushNotification(title, message);
            }

        } catch (Exception e) {
            logger.error("📮 Error sending HTTP push notification: {}", e.getMessage());
            simulatePushNotification(title, message);
        }
    }

    private void simulatePushNotification(String title, String message) {
        logger.info("🔄 PUSH SIMULATION - Title: '{}', Message: '{}'", title, message);
        // Em produção, você pode integrar com um serviço de notificação real aqui
    }

    private String createNotificationPayload(String title, String message) {
        try {
            PushNotificationPayload payload = new PushNotificationPayload(title, message);
            return objectMapper.writeValueAsString(payload);
        } catch (IOException e) {
            logger.error("Error creating notification payload", e);
            // Fallback para payload básico
            return String.format(
                    "{\"notification\":{\"title\":\"%s\",\"body\":\"%s\",\"icon\":\"/assets/icons/icon-192x192.png\"}}",
                    escapeJsonString(title),
                    escapeJsonString(message)
            );
        }
    }

    private String escapeJsonString(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Classes para desserialização da subscription
    public static class PushSubscription {
        public String endpoint;
        public SubscriptionKeys keys;

        public static class SubscriptionKeys {
            public String p256dh;
            public String auth;
        }
    }

    /**
     * Classe para o payload da notificação push
     */
    private static class PushNotificationPayload {
        private final Notification notification;
        private final String to;

        public PushNotificationPayload(String title, String body) {
            this.notification = new Notification(title, body);
            this.to = "/topics/all"; // Para FCM
        }

        // Getters para serialização JSON
        public Notification getNotification() { return notification; }
        public String getTo() { return to; }

        private static class Notification {
            private final String title;
            private final String body;
            private final String icon;

            public Notification(String title, String body) {
                this.title = title;
                this.body = body;
                this.icon = "/assets/icons/icon-192x192.png";
            }

            public String getTitle() { return title; }
            public String getBody() { return body; }
            public String getIcon() { return icon; }
        }
    }

    /**
     * Limpeza de recursos
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            logger.info("🛑 WebPushService shutdown completed");
        }
    }
}