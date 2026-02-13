package com.schoolagenda.application.web.dto;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record GradeStudentDTO(
        Long userId,
        String name,
        BigDecimal score,
        String feedback
) {}