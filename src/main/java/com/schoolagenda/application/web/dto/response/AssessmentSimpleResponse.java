package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSimpleResponse {
    private Long id;
    private String title;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;

    private BigDecimal maxScore;
    private BigDecimal weight;
    private Boolean published;
}
