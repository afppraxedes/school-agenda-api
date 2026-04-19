//package com.schoolagenda.application.web.dto.response;
//
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//import com.schoolagenda.application.web.dto.response.base.BaseAuditableResponse;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@EqualsAndHashCode(callSuper = false)
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@JsonPropertyOrder({
//        "id", "fullName", "birthDate", "className", "profilePhoto", "registrationDate", "age", "schoolClass", "feedback"
//})
//public class StudentResponse extends BaseAuditableResponse {
//    private Long id;
//    private String fullName;
//    private LocalDate birthDate;
//    private String className;
//    private String profilePhoto;
//    private LocalDateTime registrationDate;
//    private int age;
//    private SchoolClassSimpleResponse schoolClass;
//    private String feedback;
//}

package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@JsonPropertyOrder({
        "id", "fullName", "birthDate", "className", "profilePhoto",
        "registrationDate", "age", "schoolClass", "feedback",
        "createdAt", "updatedAt"
})
public record StudentResponse(
        Long id,
        String fullName,
        LocalDate birthDate,
        String className,
        String profilePhoto,
        java.time.LocalDateTime registrationDate,
        int age,
        SchoolClassSimpleResponse schoolClass,
        String feedback,

        // Campos herdados da lógica de auditoria (OffsetDateTime conforme JpaConfig)
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String createdBy,
        String lastModifiedBy
) {}