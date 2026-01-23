package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.schoolagenda.application.web.dto.response.base.BaseAuditableResponse;
import com.schoolagenda.domain.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "id", "title", "description", "startDate", "endDate", "allDay", "type",
        "schoolClassId", "schoolClassName", "location"
//        "createdBy", "lastModifiedBy", "createdAt", "updatedAt"
})
@OrderedResponse
public class SchoolEventResponse extends BaseAuditableResponse {

    private Long id;
    private String title;
    private String description;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private boolean allDay;
    private EventType type;
    private Long schoolClassId;
    private String schoolClassName;
    private String location;

}
