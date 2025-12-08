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

    @DecimalMin(value = "0.01", message = "A nota máxima deve ser maior que zero")
    @Digits(integer = 3, fraction = 2, message = "A nota máxima deve ter no máximo 3 inteiros e 2 decimais")
    private BigDecimal maxScore = new BigDecimal("10.00");

    private Boolean published = false;

    private Long createdByUserId;
}