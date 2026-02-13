package com.schoolagenda.domain.repository;

import com.schoolagenda.domain.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {

    Optional<PushSubscription> findByEndpoint(String endpoint);

    List<PushSubscription> findByUserId(Long userId);

    Optional<PushSubscription> findByEndpointAndUserId(String endpoint, Long userId);

    void deleteByEndpointAndUserId(String endpoint, Long userId);

    // Método para contar subscriptions por usuário
    long countByUserId(Long userId);

    boolean existsByUserEmail(String email);
}