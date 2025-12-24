package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponse {

    private Long id;
    private String name;
    private String schoolYear;
    private UserSimpleResponse teacher;
    private Boolean active;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime updatedAt;

    public String getDisplayName() {
        if (schoolYear != null && !schoolYear.isBlank()) {
            return name + " (" + schoolYear + ")";
        }
        return name;
    }
}