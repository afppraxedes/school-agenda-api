package com.schoolagenda.application.web.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

// DTO para resposta paginada
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponse<T> {

    private List<T> content;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private boolean first;
    private boolean last;
    private boolean empty;

    // NOVOS CAMPOS para ordenação
    private List<SortInfo> sort;
    private boolean sorted;

    // Classe interna para informações de ordenação
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortInfo {
        private String property;
        private String direction; // "ASC", "DESC"
        private boolean ignoreCase;
        private String nullHandling; // "NATIVE", "NULLS_FIRST", "NULLS_LAST"

        public static SortInfo from(Sort.Order order) {
            return new SortInfo(
                    order.getProperty(),
                    order.getDirection().name(),
                    order.isIgnoreCase(),
                    order.getNullHandling().name()
            );
        }
    }

    public static <T> PaginationResponse<T> of(Page<T> page) {
        // Converte a ordenação do Spring para nossa estrutura
        List<SortInfo> sortInfo = page.getSort().stream()
                .map(SortInfo::from)
                .collect(Collectors.toList());

        return new PaginationResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty(),
                sortInfo,
                page.getSort().isSorted()
        );
    }

    // Método auxiliar para pegar ordenação como string (útil para frontend)
    public String getSortAsString() {
        if (sort == null || sort.isEmpty()) {
            return "";
        }
        return sort.stream()
                .map(s -> s.getProperty() + "," + s.getDirection())
                .collect(Collectors.joining(";"));
    }

    // Verifica se está ordenado por uma propriedade específica
    public boolean isSortedBy(String property) {
        if (sort == null) return false;
        return sort.stream()
                .anyMatch(s -> s.getProperty().equalsIgnoreCase(property));
    }

    // Obtém direção de uma propriedade específica
    public String getDirectionFor(String property) {
        if (sort == null) return null;
        return sort.stream()
                .filter(s -> s.getProperty().equalsIgnoreCase(property))
                .map(SortInfo::getDirection)
                .findFirst()
                .orElse(null);
    }

//    public static <T> PaginationResponse<T> of(Page<T> page) {
//        return new PaginationResponse<>(
//                page.getContent(),
//                page.getNumber(),
//                page.getTotalPages(),
//                page.getTotalElements(),
//                page.getSize(),
//                page.isFirst(),
//                page.isLast(),
//                page.isEmpty()
//        );
//    }
}