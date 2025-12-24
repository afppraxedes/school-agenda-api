package com.schoolagenda.application.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchoolClassSimpleResponse {

    private Long id;
    private String name;
    private String description;
    private Integer year;
//    private Boolean isActive;
//    private Long coordinatorId;

}
