package com.schoolagenda.domain.model;

import com.schoolagenda.domain.enums.NotificationType;
import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor
public class Notification extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "action_url")
    private String url; // URL para redirecionamento ao clicar (ex: /messages/view/1)

    @Column(name = "icon_url")
    private String icon; // Ícone específico da notificação

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_read")
    private boolean read = false;

    @Column(name = "notified_at", nullable = false)
    private OffsetDateTime notifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private NotificationType type;

    public Notification(String title, String message, User user, NotificationType type, String url) {
        this.title = title;
        this.message = message;
        this.user = user;
        this.type = type;
        this.url = url;
    }
}