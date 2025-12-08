package com.schoolagenda.application.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectSimpleResponse {
    private Long id;
    private String name;
    private String schoolYear;
    private Boolean active;
}
