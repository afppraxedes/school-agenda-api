package com.schoolagenda.application.web.dto.common.assessment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentFilterRequest {

    private String title;
    private Long subjectId;
    private Long createdById;
    private Boolean published;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDateFrom;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDateTo;

    // Métodos auxiliares (opcionais mas úteis)
    public boolean hasTitle() {
        return title != null && !title.trim().isEmpty();
    }

    public boolean hasDateRange() {
        return dueDateFrom != null && dueDateTo != null;
    }

    public boolean isDateRangeValid() {
        if (!hasDateRange()) return true;
        return !dueDateFrom.isAfter(dueDateTo);
    }
}