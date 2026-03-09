package com.schoolagenda.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.schoolagenda.application.web.dto.request.PushSubscriptionRequest;
import com.schoolagenda.domain.model.PushSubscription;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.PushSubscriptionRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.PushSubscriptionService;
import com.schoolagenda.domain.service.WebPushService;
import com.schoolagenda.infrastructure.external.webpush.WebPushServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
//@Slf4j
@RequiredArgsConstructor
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    // TODO: Colocar o "@Slf4j2", conforme o "FBE"!
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;
    private final WebPushServiceImpl webPushService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void subscribe(User user, PushSubscriptionRequest subscriptionDTO) {
        try {
//            log.info("Subscribing user {} to push notifications", user.getEmail());

            // Validar dados obrigatórios
            if (subscriptionDTO.endpoint() == null || subscriptionDTO.endpoint().isEmpty()) {
                throw new IllegalArgumentException("Endpoint cannot be null or empty");
            }

            if (subscriptionDTO.keys() == null ||
                    subscriptionDTO.keys().p256dh() == null ||
                    subscriptionDTO.keys().auth() == null) {
                throw new IllegalArgumentException("Push subscription keys are required");
            }

            // Verificar se já existe subscription para este endpoint
            Optional<PushSubscription> existingSubscription =
                    pushSubscriptionRepository.findByEndpoint(subscriptionDTO.endpoint());

            if (existingSubscription.isPresent()) {
                // Atualizar subscription existente
                PushSubscription subscription = existingSubscription.get();
                subscription.setP256dh(subscriptionDTO.keys().p256dh());
                subscription.setAuth(subscriptionDTO.keys().auth());
                subscription.setUser(user);
                pushSubscriptionRepository.save(subscription);
                logger.info("Updated existing push subscription for user: {}", user.getUsername());
            } else {
                // Criar nova subscription
                PushSubscription subscription = new PushSubscription();
                subscription.setEndpoint(subscriptionDTO.endpoint());
                subscription.setExpirationTime(subscriptionDTO.expirationTime());
                subscription.setP256dh(subscriptionDTO.keys().p256dh());
                subscription.setAuth(subscriptionDTO.keys().auth());
                subscription.setUser(user);
                pushSubscriptionRepository.save(subscription);
                logger.info("Created new push subscription for user: {}", user.getUsername());
            }

        } catch (Exception e) {
            logger.error("Error subscribing user to push notifications: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to subscribe to push notifications: " + e.getMessage());
        }
    }

    /**
     * Remove a inscrição de push notifications para um usuário e endpoint específicos
     */
    @Override
    @Transactional
    public void unsubscribe(User user, String endpoint) {
        try {
//            log.info("Unsubscribing user {} from push notifications for endpoint: {}",
//                    user.getEmail(), endpoint);
            logger.info("Unsubscribing user {} from push notifications for endpoint: {}",
                    user.getUsername(), endpoint);

            // Validar parâmetros
            if (endpoint == null || endpoint.trim().isEmpty()) {
                throw new IllegalArgumentException("Endpoint cannot be null or empty");
            }

            // Buscar a subscription
            Optional<PushSubscription> subscription = pushSubscriptionRepository
                    .findByEndpointAndUserId(endpoint, user.getId());

            if (subscription.isPresent()) {
                // Remover a subscription
                pushSubscriptionRepository.delete(subscription.get());
//                logger.info("Successfully unsubscribed user {} from endpoint: {}",
//                        user.getEmail(), endpoint);
                logger.info("Successfully unsubscribed user {} from endpoint: {}",
                        user.getUsername(), endpoint);
            } else {
                logger.warn("No push subscription found for user {} with endpoint: {}",
                        user.getUsername(), endpoint);
                // Não lançar exceção, apenas logar - pode ser que já foi removido
            }

        } catch (Exception e) {
//            log.error("Error unsubscribing user from push notifications: {}", e.getMessage(), e);
            logger.error("Error unsubscribing user from push notifications: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to unsubscribe from push notifications: " + e.getMessage());
        }
    }

    /**
     * Remove todas as inscrições de push notifications de um usuário
     */
    @Override
    @Transactional
    public void unsubscribeAll(User user) {
        try {
//            logger.info("Unsubscribing user {} from all push notifications", user.getEmail());
            logger.info("Unsubscribing user {} from all push notifications", user.getUsername());

            List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserId(user.getId());

            if (!subscriptions.isEmpty()) {
                pushSubscriptionRepository.deleteAll(subscriptions);
                logger.info("Successfully unsubscribed user {} from {} push subscriptions",
//                        user.getEmail(), subscriptions.size());
                        user.getUsername(), subscriptions.size());
            } else {
//                logger.info("No push subscriptions found for user {}", user.getEmail());
                logger.info("No push subscriptions found for user {}", user.getUsername());
            }

        } catch (Exception e) {
            logger.error("Error unsubscribing user from all push notifications: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to unsubscribe from all push notifications: " + e.getMessage());
        }
    }

    /**
     * Remove subscription por endpoint (sem verificar usuário - para admin)
     */
    @Override
    @Transactional
    public void unsubscribeByEndpoint(String endpoint) {
        try {
            logger.info("Unsubscribing from push notifications for endpoint: {}", endpoint);

            if (endpoint == null || endpoint.trim().isEmpty()) {
                throw new IllegalArgumentException("Endpoint cannot be null or empty");
            }

            Optional<PushSubscription> subscription = pushSubscriptionRepository.findByEndpoint(endpoint);

            if (subscription.isPresent()) {
                pushSubscriptionRepository.delete(subscription.get());
                logger.info("Successfully unsubscribed endpoint: {}", endpoint);
            } else {
                logger.warn("No push subscription found for endpoint: {}", endpoint);
            }

        } catch (Exception e) {
            logger.error("Error unsubscribing by endpoint: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to unsubscribe by endpoint: " + e.getMessage());
        }
    }

    public boolean isUserSubscribed(String email) {
        // Busca o usuário e verifica se existe uma entrada na tabela de push_notifications
        return pushSubscriptionRepository.existsByUserEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserSubscribedById(Long userId) {
        try {
            // Simples e direto: se houver 1 ou mais registros na tabela, retorna true
            boolean exists = pushSubscriptionRepository.existsByUserId(userId);

            logger.info("🔍 Verificando status de subscrição para UserID {}: {}", userId, exists ? "INSCRITO" : "NÃO INSCRITO");

            return exists;
        } catch (Exception e) {
            logger.error("❌ Erro ao verificar status de push para o usuário {}: {}", userId, e.getMessage());
            return false; // Fail-safe: assume que não está inscrito em caso de erro no banco
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveSubscriptions() {
        try {
            return pushSubscriptionRepository.countUniqueActiveSubscriptions();
        } catch (Exception e) {
            logger.error("❌ Erro ao contar assinaturas push: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public void sendPushToUser(Long userId, String title, String message) {
        List<PushSubscription> subs = pushSubscriptionRepository.findByUserId(userId);

        if (subs.isEmpty()) {
            logger.info("ℹ️ Usuário {} não possui dispositivos registrados para Push.", userId);
            return;
        }

        subs.forEach(sub -> {
            try {
                // 1. Instancia o Record seguindo a estrutura fornecida
                var pushRequest = new PushSubscriptionRequest(
                        sub.getEndpoint(),
                        null, // expirationTime do seu Record (pode ser null)
                        new PushSubscriptionRequest.KeysDTO(
                                sub.getP256dh(),
                                sub.getAuth()
                        )
                );

                // 2. Converte para JSON String
                String subscriptionJson = objectMapper.writeValueAsString(pushRequest);

                // 3. Dispara para o seu WebPushServiceImpl de 3 argumentos
                webPushService.sendNotification(subscriptionJson, title, message);

            } catch (JsonProcessingException e) {
                logger.error("❌ Erro ao processar JSON para o usuário {}: {}", userId, e.getMessage());
            } catch (Exception e) {
                logger.error("❌ Falha no envio de Push (Sub ID {}): {}", sub.getId(), e.getMessage());
            }
        });
    }
}
