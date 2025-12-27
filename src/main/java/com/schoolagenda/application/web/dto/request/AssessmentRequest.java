package com.schoolagenda.application.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentRequest {

    @NotBlank(message = "O título da avaliação é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "A disciplina é obrigatória")
    private Long subjectId;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;

    @NotNull
    @DecimalMin(value = "0.1", message = "A nota máxima deve ser maior que zero")
    @Digits(integer = 3, fraction = 2, message = "A nota máxima deve ter no máximo 3 inteiros e 2 decimais")
    private BigDecimal maxScore = new BigDecimal("10.00");

    @NotNull
    @DecimalMin(value = "0.1", message = "o peso máximo deve ser maior que zero")
    @Digits(integer = 1, fraction = 2, message = "o peso deve ter no máximo 1 inteiro e 2 decimais")
    private BigDecimal weight;

    private boolean isRecovery;

    private Boolean published = false;

    private Long createdByUserId;

    // CAMPOS DA ÚLTIMA IMPLEMENTAÇÃO SIGERIDA PELO "GEMINI"! VERIFICAR SE UTILIZO SOMENTE OS CAMPOS ABAIXO!
//    @NotBlank String title,
//    @NotNull Long subjectId,
//    @NotNull BigDecimal maxScore,
//    @NotNull BigDecimal weight,
//    boolean isRecovery, // Novo campo
//    LocalDateTime date
}