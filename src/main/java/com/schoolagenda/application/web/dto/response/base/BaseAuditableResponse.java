package com.schoolagenda.application.web.dto.response.base;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

// Opcional: se quiser garantir a ordem mesmo quando usada diretamente
//@JsonPropertyOrder(value = {
//        "createdBy", "lastModifiedBy", "createdAt", "updatedAt"
//}, alphabetic = false)
@Getter
@Setter
public abstract class BaseAuditableResponse {

    private String createdBy;
    private String lastModifiedBy;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}