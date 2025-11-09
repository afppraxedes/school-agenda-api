package com.schoolagenda.domain.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

// src/main/java/com/schoolagenda/api/entity/PushSubscription.java
@Entity
// TODO: COLOCAR O NOME DAS TABELAS NO SINGULAR QUANDO FOR UTILIZAR O "MYSQL" (push_subscription)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PushSubscription() {
        this.createdAt = LocalDateTime.now();
    }

    public PushSubscription(Long id, String endpoint, String p256dh, String auth, User user, LocalDateTime createdAt) {
        this.id = id;
        this.endpoint = endpoint;
        this.p256dh = p256dh;
        this.auth = auth;
        this.user = user;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getP256dh() {
        return p256dh;
    }

    public void setP256dh(String p256dh) {
        this.p256dh = p256dh;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}