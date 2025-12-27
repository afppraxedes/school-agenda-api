package com.schoolagenda.application.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private Long id;

    // Dados do Aluno
    private Long studentId;
    private String studentName;

    // Dados da Disciplina
    private Long subjectId;
    private String subjectName;

    // Dados da Frequência
    private LocalDate date;
    private boolean present;
    private String note; // Justificativas ou observações
}
