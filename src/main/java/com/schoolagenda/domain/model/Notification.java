package com.schoolagenda.domain.model;

import com.schoolagenda.domain.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
// TODO: COLOCAR O NOME DAS TABELAS NO SINGULAR QUANDO FOR UTILIZAR O "MYSQL" (notifications)
@Table(name = "notifications")
// 4. Herda da classe base para obter os campos created_by, updated_at, etc.
public class Notification /*extends BaseAuditableEntity*/ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean read = false;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String title, String message, User user, NotificationType type) {
        this();
        this.title = title;
        this.message = message;
        this.user = user;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
}