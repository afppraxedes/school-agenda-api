package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

// TODO: converter para "records". Para todos os "Requests", converter para "record"!
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeRequest {

    @NotNull(message = "A avaliação é obrigatória")
    private Long assessmentId;

    @NotNull(message = "O estudante é obrigatório")
    private Long studentUserId;

    @DecimalMin(value = "0.00", message = "A nota não pode ser negativa")
    @DecimalMax(value = "100.00", message = "A nota não pode ser maior que 100")
    @Digits(integer = 3, fraction = 2, message = "A nota deve ter no máximo 3 inteiros e 2 decimais")
    private BigDecimal score;

    @Size(max = 1000, message = "O feedback não pode exceder 1000 caracteres")
    private String feedback;

    // Campos de status (Ausência/Justificativa)
    private Boolean absent;

    private Boolean excused;

    // NOTA: O campo gradedByUserId foi removido por segurança.
    // O sistema usará o usuário autenticado.
}