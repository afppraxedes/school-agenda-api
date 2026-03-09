package com.schoolagenda.application.web.controller;

import com.schoolagenda.application.web.dto.response.AdminDashboardResponse;
import com.schoolagenda.domain.service.MessageService;
import com.schoolagenda.domain.service.PushSubscriptionService;
import com.schoolagenda.domain.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard/admin")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final UserService userService;
    private final MessageService messageService;
    private final PushSubscriptionService pushService;

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<AdminDashboardResponse> getStats() {
        long totalUsers = userService.getTotalUsersCount();
        long activePush = pushService.countActiveSubscriptions();
        long messages24h = messageService.countMessagesInLast24Hours();

        return ResponseEntity.ok(new AdminDashboardResponse(totalUsers, activePush, messages24h));
    }

}
