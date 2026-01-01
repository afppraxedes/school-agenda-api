package com.schoolagenda.application.web.dto.response;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long senderId,
        String senderName,
        Long recipientId,
        String recipientName,
        Long studentId,
        String studentName,
        String subject,
        String content,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {}
