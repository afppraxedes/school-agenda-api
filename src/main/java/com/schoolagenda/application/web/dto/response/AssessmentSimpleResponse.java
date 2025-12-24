package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSimpleResponse {
    private Long id;
    private String title;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dueDate;

    private Double maxScore;
    private Boolean published;
}
