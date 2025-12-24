// src/main/java/com/schoolagenda/application/web/dto/AnnouncementResponse.java
package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.schoolagenda.application.web.dto.response.base.BaseAuditableResponse;
import com.schoolagenda.domain.model.Announcement.AnnouncementType;
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
        "id", "title", "description", "imagePath", "type", "orderPosition", "isActive"
})
@OrderedResponse
public class AnnouncementResponse extends BaseAuditableResponse {

    private Long id;
    private String title;
    private String description;
    private String imagePath;
    private AnnouncementType type;
    private Integer orderPosition;
    private Boolean isActive;

}