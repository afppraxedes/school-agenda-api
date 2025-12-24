// src/main/java/com/schoolagenda/application/web/dto/StudentRequest.java
package com.schoolagenda.application.web.dto.request;

import com.schoolagenda.domain.model.SchoolClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Class name is required")
    private String className;

    private String profilePhoto;

//    private Long schoolClassId;
    private SchoolClass schoolClass;
}