package com.schoolagenda.application.web.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
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

    private String feedback;

    private Long gradedByUserId;

    // Ausente
    private Boolean absent = false;

    // Justificado
    private Boolean excused = false;
}
