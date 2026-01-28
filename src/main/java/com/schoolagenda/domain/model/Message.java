package com.schoolagenda.domain.model;

import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class Message extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id") // Contexto da mensagem
    private Student student;

    @Column(nullable = false)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "attachment_name")
    private String attachmentName; // Opcional: para exibir "Exercicio.pdf" na UI

//    @Column(name = "read_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime readAt;

    private OffsetDateTime deletedAt;

    private boolean archivedBySender;
    private boolean archivedByRecipient;
}
