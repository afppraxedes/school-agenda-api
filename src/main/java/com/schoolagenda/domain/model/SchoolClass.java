package com.schoolagenda.domain.model;

import com.schoolagenda.domain.model.base.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "school_classes")
@Getter
@Setter
@NoArgsConstructor
public class SchoolClass extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name; // Ex: "7º Ano B - 2025"

    @Column(columnDefinition = "TEXT")
    private String description; // Descrição opcional da turma

    @Column(nullable = false)
    private Integer year; // Ano letivo (para históricos)

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // Se a turma está ativa neste ano

    // TODO: APÓS OS TESTES, COLOCAR O RELACIONAMENTO ABAIXO PARA DEFINIR O "COORDENADOR RESPONSÁVEL"!
    // Relacionamentos:
    // Opcional: Coordenador/Diretor responsável pela turma
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "coordinator_id")
     private User coordinator;
}
