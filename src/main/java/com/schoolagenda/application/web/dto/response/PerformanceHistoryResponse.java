package com.schoolagenda.application.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO: Renomear para "ActiveClassDTO" e colocar num outro pacote "dto" para manter a organização!
public class PerformanceHistoryResponse {
    private List<String> labels; // Ex: ["Turma A", "Turma B"]
    private List<Double> values; // Ex: [8.5, 7.2]
}
