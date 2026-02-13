package com.schoolagenda.application.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO: Renomear para "ActiveClassDTO" e colocar num outro pacote "dto" para manter a organização!
public class TeacherAlertResponse {
    private String type; // 'GRADE', 'ATTENDANCE', 'MEETING'
    private String message;
    private String deadline;
    private String priority; // 'HIGH', 'MEDIUM', 'LOW'
}
