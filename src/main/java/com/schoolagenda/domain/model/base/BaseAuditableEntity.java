package com.schoolagenda.domain.model.base;

//package com.schoolagenda.agenda.api.domain.model.base; // Crie uma pasta 'base'

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // 2. Adiciona o Listener para esta classe
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
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Quando foi modificada por último
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
