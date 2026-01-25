package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.schoolagenda.application.web.dto.response.base.BaseAuditableResponse;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "id", "teacherClassId", "teacherName", "subjectName", "schoolClassName", "dayOfWeek",
        "startTime", "endTime", "roomName"
})
@OrderedResponse
public class TimetableResponse extends BaseAuditableResponse {
    public Long id;
    public Long teacherClassId;
    public String teacherName;
    public String subjectName;
    public String schoolClassName;
    public DayOfWeek dayOfWeek;
    public LocalTime startTime;
    public LocalTime endTime;
    public String roomName;
}
