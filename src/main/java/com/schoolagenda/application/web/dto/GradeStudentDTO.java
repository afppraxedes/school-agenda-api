package com.schoolagenda.application.web.dto;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record GradeStudentDTO(
        Long studentId,
        String name,
        BigDecimal grade1,
        BigDecimal grade2,
        BigDecimal grade3,
        BigDecimal grade4,
        BigDecimal average,
        String feedback
//        Boolean isAbsent,
//        Boolean isExcused
) {}