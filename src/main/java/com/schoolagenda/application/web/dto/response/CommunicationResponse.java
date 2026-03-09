package com.schoolagenda.application.web.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
public record CommunicationResponse(
//        Long id,
//        String title,
//        String content,
//        String date,
//        String type, // 'comunicado', 'mensagem', 'aviso'
//        String icon,
//        String color,
//        String attachmentUrl

        Long id,
        String title,
        String content,
        String type,        // 'comunicado', 'mensagem', 'aviso'
        OffsetDateTime notifiedAt,   // Data formatada para o frontend
        String attachmentUrl,
        String attachmentName
) {}