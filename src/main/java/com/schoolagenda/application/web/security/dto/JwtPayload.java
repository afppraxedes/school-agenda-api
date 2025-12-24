package com.schoolagenda.application.web.security.dto;

import com.schoolagenda.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

public record JwtPayload(
        String subject,     // email
        String id,
        String name,
        String email,
        List<String> roles
) {
    public static JwtPayload fromUser(User user) {
        return new JwtPayload(
                user.getEmail(),
                user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(Enum::name)
                        .collect(Collectors.toList())
        );
    }
}
