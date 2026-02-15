package com.schoolagenda.application.web.dto.response;

import com.schoolagenda.application.web.dto.StudentSummaryDTO;
import lombok.Builder;

import java.util.List;

@Builder
public record ResponsibleDashboardResponse(
        String responsibleName,
        long unreadMessagesCount,
        List<StudentSummaryDTO> children
) {}
