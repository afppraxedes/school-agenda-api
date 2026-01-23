package com.schoolagenda.application.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeacherClassResponse {
    private Long id;

    // Dados do Professor
    private Long teacherId;
    private String teacherName;

    // Dados da Disciplina
    private Long subjectId;
    private String subjectName;

    // Dados da Turma (Substituindo o antigo className)
    private Long schoolClassId;
    private String schoolClassName;

    private OffsetDateTime createdAt;
    private String createdBy;
}