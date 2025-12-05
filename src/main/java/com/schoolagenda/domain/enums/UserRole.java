package com.schoolagenda.domain.enums;

import java.util.Arrays;

public enum UserRole {

    ADMINISTRATOR ("ADMINISTRATOR"), // NOVO: Para gestão técnica do sistema
    DIRECTOR ("DIRECTOR"),
    TEACHER ("TEACHER"),
    RESPONSIBLE ("RESPONSIBLE"),
    STUDENT ("STUDENT");       // NOVO: Para o aluno acessar seus próprios dados

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