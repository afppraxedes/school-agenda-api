package com.schoolagenda.application.web.dto.response;

public record AdminDashboardResponse(
        long totalUsers,
        long activePushSubscriptions,
        long messagesLast24h
) {}
