package com.schoolagenda.domain.service.impl;

import com.schoolagenda.application.web.dto.request.PushSubscriptionRequest;
import com.schoolagenda.domain.model.PushSubscription;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.PushSubscriptionRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.PushSubscriptionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
//@Slf4j
@Transactional
public class PushSubscriptionServiceImpl implements PushSubscriptionService {

    // TODO: Colocar o "@Slf4j2", conforme o "FBE"!
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final UserRepository userRepository;

    public PushSubscriptionServiceImpl(PushSubscriptionRepository pushSubscriptionRepository, UserRepository userRepository) {
        this.pushSubscriptionRepository = pushSubscriptionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void subscribe(User user, PushSubscriptionRequest subscriptionDTO) {
        try {
//            log.info("Subscribing user {} to push notifications", user.getEmail());

            // Validar dados obrigatórios
            if (subscriptionDTO.getEndpoint() == null || subscriptionDTO.getEndpoint().isEmpty()) {
                throw new IllegalArgumentException("Endpoint cannot be null or empty");
            }

            if (subscriptionDTO.getKeys() == null ||
                    subscriptionDTO.getKeys().getP256dh() == null ||
                    subscriptionDTO.getKeys().getAuth() == null) {
                throw new IllegalArgumentException("Push subscription keys are required");
            }

            // Verificar se já existe subscription para este endpoint
            Optional<PushSubscription> existingSubscription =
                    pushSubscriptionRepository.findByEndpoint(subscriptionDTO.getEndpoint());

            if (existingSubscription.isPresent()) {
                // Atualizar subscription existente
                PushSubscription subscription = existingSubscription.get();
                subscription.setP256dh(subscriptionDTO.getKeys().getP256dh());
                subscription.setAuth(subscriptionDTO.getKeys().getAuth());
                subscription.setUser(user);
                pushSubscriptionRepository.save(subscription);
                logger.info("Updated existing push subscription for user: {}", user.getUsername());
            } else {
                // Criar nova subscription
                PushSubscription subscription = new PushSubscription();
                subscription.setEndpoint(subscriptionDTO.getEndpoint());
                subscription.setP256dh(subscriptionDTO.getKeys().getP256dh());
                subscription.setAuth(subscriptionDTO.getKeys().getAuth());
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
}
