package com.schoolagenda.application.web.dto.common.subject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectFilterRequest {

    private String name;
    private String schoolYear;
    private Long teacherId;
    private Boolean active;

    // Métodos atualizados (remover hasXFilter())
    public boolean hasName() {
        return StringUtils.hasText(name);
    }

    public boolean hasSchoolYear() {
        return StringUtils.hasText(schoolYear);
    }

    public boolean hasTeacher() {
        return teacherId != null;
    }

    public boolean hasActive() {
        return active != null;
    }

    // OU simplesmente use StringUtils.hasText() direto
}
