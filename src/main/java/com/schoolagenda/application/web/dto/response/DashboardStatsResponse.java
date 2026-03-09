package com.schoolagenda.application.web.dto.response;

public record DashboardStatsResponse(
        long totalUsers,
        long activePushSubscriptions,
        long messagesLast24h,
        long systemAlerts
) {}
