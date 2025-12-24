package com.schoolagenda.domain.enums;

import java.util.Arrays;

public enum NotificationType {
    MESSAGE ("MESSAGE"),
    ALERT ("ALERT"),
    REMINDER ("REMINDER"),
    ANNOUNCEMENT ("ANNOUNCEMENT");

    private final String description;

    NotificationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public static NotificationType toEnum(final String description) {
        return Arrays.stream(NotificationType.values())
                .filter(notificationType -> notificationType.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid description: " + description));
    }

}