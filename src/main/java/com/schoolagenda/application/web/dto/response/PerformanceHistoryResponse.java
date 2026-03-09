package com.schoolagenda.application.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// TODO: Renomear para "ActiveClassDTO" e colocar num outro pacote "dto" para manter a organização!
public class PerformanceHistoryResponse {
    private String studentName;     // Nome do aluno para o título do gráfico
    private List<String> labels;     // ["B1", "B2", "B3", "B4"]
    private List<BigDecimal> values; // [9.8, 8.0, 0.0, 0.0]
    private boolean needsRecovery;
}


//public record PerformanceHistoryResponse(
//        String studentName,      // Nome do aluno para o título do gráfico
//        List<String> labels,     // ["B1", "B2", "B3", "B4"]
//        List<BigDecimal> values, // [9.8, 8.0, 0.0, 0.0]
//        boolean needsRecovery    // Lógica calculada no servidor
//) {}