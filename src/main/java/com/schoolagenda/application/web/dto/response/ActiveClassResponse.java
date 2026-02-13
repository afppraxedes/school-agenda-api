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
public class ActiveClassResponse {
    private String id;
    private String name;
    private String subject;
    private Integer studentCount;
    private String nextLesson;
}
