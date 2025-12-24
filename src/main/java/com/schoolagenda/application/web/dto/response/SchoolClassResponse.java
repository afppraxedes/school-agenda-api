package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.schoolagenda.application.web.dto.response.base.BaseAuditableResponse;
import com.schoolagenda.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "id", "name", "description", "year", "isActive", "coordinatorId"
//        "createdBy", "lastModifiedBy", "createdAt", "updatedAt"
})
@OrderedResponse
public class SchoolClassResponse extends BaseAuditableResponse {
    private Long id;
    private String name;
    private String description;
    private Integer year;
    private Boolean isActive;
    private Long coordinatorId; // DTO completo do coordenador ou apenas o id
}