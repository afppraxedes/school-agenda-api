package com.schoolagenda.domain.model;

import java.util.Arrays;

public enum UserRole {
    RESPONSIBLE ("RESPONSIBLE"),
    TEACHER ("TEACHER"),
    DIRECTOR ("DIRECTOR");

    private final String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserRole toEnum(final String description) {
        return Arrays.stream(UserRole.values())
                .filter(profileEnum -> profileEnum.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid description: " + description));
    }
}