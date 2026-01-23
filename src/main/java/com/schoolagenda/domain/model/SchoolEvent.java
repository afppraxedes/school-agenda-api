package com.schoolagenda.domain.model;

import com.schoolagenda.domain.enums.EventType;
import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "school_events")
@Getter
@Setter
@NoArgsConstructor
public class SchoolEvent extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private OffsetDateTime startDate;

    @Column(nullable = false)
    private OffsetDateTime endDate;

    @Column(nullable = false)
    private boolean allDay; // Flag para eventos de dia inteiro

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    // Se nulo, o evento é para a escola toda.
    // Se preenchido, é específico para uma turma (ex: prova).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_class_id")
    private SchoolClass schoolClass;

    private String location; // Sala 2, Auditório, Campo, etc.
}