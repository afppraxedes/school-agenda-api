package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.request.PushSubscriptionRequest;
import com.schoolagenda.domain.model.User;
import com.schoolagenda.domain.repository.PushSubscriptionRepository;
import com.schoolagenda.domain.repository.UserRepository;
import com.schoolagenda.domain.service.impl.NotificationServiceImpl;
import com.schoolagenda.domain.service.impl.PushSubscriptionServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/push")
//@Slf4j
public class PushSubscriptionController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final PushSubscriptionServiceImpl pushNotificationServiceImpl;

    private final UserRepository userRepository;

    private final PushSubscriptionRepository pushSubscriptionRepository;

    public PushSubscriptionController(PushSubscriptionServiceImpl pushNotificationServiceImpl, UserRepository userRepository, PushSubscriptionRepository pushSubscriptionRepository) {
        this.pushNotificationServiceImpl = pushNotificationServiceImpl;
        this.userRepository = userRepository;
        this.pushSubscriptionRepository = pushSubscriptionRepository;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody PushSubscriptionRequest subscriptionDTO,
                                       Authentication authentication) {
        try {
            logger.info("Received push subscription request for endpoint: {}", subscriptionDTO.getEndpoint());

            // Validar autenticação
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User must be authenticated to subscribe to push notifications");
            }

            // Obter o username/email do usuário autenticado
//            String username = authentication.getName();
            Object principal = authentication.getPrincipal();
            String username;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
            logger.info("User {} is subscribing to push notifications", username);

            // Buscar o usuário real no banco de dados
//            User user = userRepository.findByEmail(username)
//            User user = userRepository.findByUsername(username)
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + username));

            // Validar DTO
            if (subscriptionDTO == null) {
                return ResponseEntity.badRequest().body("Push subscription data is required");
            }

            if (subscriptionDTO.getEndpoint() == null || subscriptionDTO.getEndpoint().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Endpoint is required");
            }

            // Processar subscription
            pushNotificationServiceImpl.subscribe(user, subscriptionDTO);

            return ResponseEntity.ok().body(Map.of(
                    "message", "Successfully subscribed to push notifications",
//                    "user", user.getEmail()
                       "user", user.getUsername()
            ));

        } catch (Exception e) {
            logger.error("Error in push subscription: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error subscribing to push notifications",
                    "details", e.getMessage()
            ));
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribe(@RequestParam String endpoint,
                                         Authentication authentication) {
        try {
            logger.info("Received push unsubscribe request for endpoint: {}", endpoint);

            // Validar autenticação
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User must be authenticated to unsubscribe from push notifications");
            }

            // Validar endpoint
            if (endpoint == null || endpoint.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Endpoint parameter is required");
            }

            // Obter usuário autenticado
            String username = authentication.getName();
//            User user = userRepository.findByEmail(username)
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + username));

            // Processar unsubscribe
            pushNotificationServiceImpl.unsubscribe(user, endpoint);

            return ResponseEntity.ok().body(Map.of(
                    "message", "Successfully unsubscribed from push notifications",
                    "endpoint", endpoint,
//                    "user", user.getEmail()
                    "user", user.getUsername()
            ));

        } catch (Exception e) {
            logger.error("Error in push unsubscribe: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error unsubscribing from push notifications",
                    "details", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para remover todas as subscriptions de um usuário
     */
    @PostMapping("/unsubscribe-all")
    public ResponseEntity<?> unsubscribeAll(Authentication authentication) {
        try {
            logger.info("Received push unsubscribe-all request");

            // Validar autenticação
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User must be authenticated to unsubscribe from push notifications");
            }

            // Obter usuário autenticado
            String username = authentication.getName();
//            User user = userRepository.findByEmail(username)
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + username));

            // Processar unsubscribe all
            pushNotificationServiceImpl.unsubscribeAll(user);

            return ResponseEntity.ok().body(Map.of(
                    "message", "Successfully unsubscribed from all push notifications",
//                    "user", user.getEmail()
                    "user", user.getUsername()
            ));

        } catch (Exception e) {
            logger.error("Error in push unsubscribe-all: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error unsubscribing from all push notifications",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getSubscriptionStatus(Authentication authentication) {
        // 1. Obtém o username/email do principal autenticado
        String email = authentication.getName();

        // 2. Verifica a existência na camada de serviço
        boolean isSubscribed = pushNotificationServiceImpl.isUserSubscribed(email);

        // 3. Retorna o JSON: {"subscribed": true/false}
        return ResponseEntity.ok(Map.of("subscribed", isSubscribed));
    }
}