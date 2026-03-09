package com.schoolagenda.application.web.dto.request;

import java.math.BigDecimal;

// Entrada do professor
public record SingleStudentGradesRequest(
        Long studentId,
        Long teacherClassId,
        BigDecimal grade1,
        BigDecimal grade2,
        BigDecimal grade3,
        BigDecimal grade4,
        String feedback
//        Boolean isAbsent,
//        Boolean isExcused
) {}
