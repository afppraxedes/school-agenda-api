package com.schoolagenda.domain.model.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // 2. Adiciona o Listener para esta classe
@Getter
@Setter
public abstract class BaseAuditableEntity {

    // Quem criou a entidade (ID ou Username do usuário logado)
    @CreatedBy
    @Column(name = "created_by")
    private String createdBy;

    // Quem modificou por último (ID ou Username do usuário logado)
    @LastModifiedBy
    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    // Quando foi criada
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // Quando foi modificada por último
    @LastModifiedDate
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

}
