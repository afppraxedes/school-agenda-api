package com.schoolagenda.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

// src/main/java/com/schoolagenda/api/entity/PushSubscription.java
@Entity
// TODO: COLOCAR O NOME DAS TABELAS NO SINGULAR QUANDO FOR UTILIZAR O "MYSQL" (push_subscription)
@Getter
@Setter
@Table(name = "push_subscriptions")
public class PushSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String endpoint;

    @Column(name = "p256dh", nullable = false, length = 500)
    private String p256dh;

    @Column(name = "auth", nullable = false, length = 100)
    private String auth;

    @Column(name = "expiration_time")
    private Long expirationTime; // Adicionado para conformidade

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PushSubscription() {
        this.createdAt = LocalDateTime.now();
    }

    public PushSubscription(Long id, String endpoint, String p256dh, String auth, Long expirationTime, User user, LocalDateTime createdAt) {
        this.id = id;
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
        this.expirationTime = expirationTime;
        this.user = user;
        this.createdAt = createdAt;
    }
}