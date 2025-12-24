// src/main/java/com/schoolagenda/application/web/dto/TeacherClassRequest.java
package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Record para criação e atualização de vínculos entre Professor, Disciplina e Turma.
 */
public record TeacherClassRequest(
        @NotNull(message = "O professor é obrigatório")
        Long teacherId,

        @NotNull(message = "A disciplina é obrigatória")
        Long subjectId,

        @NotNull(message = "A turma é obrigatória")
        Long schoolClassId
) {}