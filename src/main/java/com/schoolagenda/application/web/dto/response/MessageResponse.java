package com.schoolagenda.application.web.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.schoolagenda.application.web.dto.response.base.BaseAuditableResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
        "id", "senderId", "senderName", "recipientId", "recipientName",
        "studentId", "studentName", "subject", "content",
        "createdAt", "readAt", "deletedAt"
})
@OrderedResponse
public class MessageResponse extends BaseAuditableResponse {
    public Long id;
    public Long senderId;
    public String senderName;
    public Long recipientId;
    public String recipientName;
    public Long studentId;
    public String studentName;
    public String subject;
    public String content;
    public OffsetDateTime createdAt;
    public OffsetDateTime readAt;
    private OffsetDateTime deletedAt;
}
