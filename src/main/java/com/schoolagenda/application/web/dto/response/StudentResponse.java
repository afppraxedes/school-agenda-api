package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.schoolagenda.application.web.dto.response.base.BaseAuditableResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "id", "fullName", "birthDate", "className", "profilePhoto", "registrationDate", "age", "schoolClass"
})
public class StudentResponse extends BaseAuditableResponse {
    private Long id;
    private String fullName;
    private LocalDate birthDate;
    private String className;
    private String profilePhoto;
    private LocalDateTime registrationDate;
    private int age;
    private SchoolClassSimpleResponse schoolClass;
}