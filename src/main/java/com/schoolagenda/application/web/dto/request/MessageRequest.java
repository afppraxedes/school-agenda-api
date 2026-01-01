package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MessageRequest(
        @NotNull Long recipientId,
        Long studentId, // Opcional: Contexto do aluno (ex: Professor falando com o Pai sobre o Aluno X)
        @NotBlank @Size(max = 200) String subject,
        @NotBlank String content
) {}
