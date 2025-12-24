package com.schoolagenda.application.web.dto.common.grade;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GradeStatistics {
    private Long assessmentId;
    private int totalStudents;
    private int gradedCount;
    private int absentCount;
    private BigDecimal averageScore;
    private BigDecimal highestScore;
    private BigDecimal lowestScore;
    private int passingCount;
    private BigDecimal passingRate; // porcentagem
}