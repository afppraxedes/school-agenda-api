package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String title;
    private String message;
    private Long userId;
    private String userName;
    private boolean read;
    private LocalDateTime createdAt;
    private NotificationType type;
    private String userRole;

}
