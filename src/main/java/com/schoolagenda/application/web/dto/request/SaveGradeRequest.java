package com.schoolagenda.application.web.dto.request;

import java.math.BigDecimal;
import java.util.Optional;

public record SaveGradeRequest(
        Long studentId,
        Long teacherClassId,
        BigDecimal grade1,
        BigDecimal grade2,
        BigDecimal grade3,
        BigDecimal grade4,
        String feedback,
        Optional<Boolean> isAbsent, // Está como "Optional", pois será utilizado mais pra frente!
        Optional<Boolean> isExcused // Está como "Optional", pois será utilizado mais pra frente!
) {}
