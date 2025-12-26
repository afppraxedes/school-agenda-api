package com.schoolagenda.domain.enums;

public enum AcademicStatus {
    APROVADO("Aprovado"),
    RECUPERACAO("Em Recuperação"),
    REPROVADO("Reprovado"),
    EM_CURSO("Em Curso");

    private final String description;
    AcademicStatus(String description) { this.description = description; }
    public String getDescription() { return description; }
}
