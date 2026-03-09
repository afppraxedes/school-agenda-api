package com.schoolagenda.application.web.dto;

import java.math.BigDecimal;

/**
 * DTO para consolidar as notas de uma disciplina específica no boletim.
 */
public record ReportCardGradeDTO(
        String subject,      // Nome da Disciplina (Ex: Matemática)
        BigDecimal b1,       // Nota do 1º Bimestre
        BigDecimal b2,       // Nota do 2º Bimestre
        BigDecimal b3,       // Nota do 3º Bimestre
        BigDecimal b4,       // Nota do 4º Bimestre
        BigDecimal average   // Média Final da Disciplina
) {}