package com.schoolagenda.application.web.dto.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

// DTO para paginação
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {

    @Min(value = 0, message = "Página deve ser maior ou igual a 0")
    private Integer page = 0;

    @Min(value = 1, message = "Tamanho da página deve ser pelo menos 1")
    @Max(value = 100, message = "Tamanho da página não pode exceder 100")
    private Integer size = 20;

    // Campos para ordenação simples (para compatibilidade)
    private String sortBy = "id";
    private Sort.Direction direction = Sort.Direction.ASC;

    // NOVO: Suporte a ordenação múltipla
    private List<SortOrder> sortOrders = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortOrder {
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Propriedade de ordenação inválida")
        private String property;

        private Sort.Direction direction = Sort.Direction.ASC;

        @Pattern(regexp = "^(NATIVE|NULLS_FIRST|NULLS_LAST)$",
                message = "Null handling deve ser: NATIVE, NULLS_FIRST ou NULLS_LAST")
        private String nullHandling = "NATIVE";

        private Boolean ignoreCase = false;

        public SortOrder(String property, Sort.Direction direction) {
            // INCLUSO APENAS PARA MANTER A COMPATIBILIDADE COM O MÉTODO "addSort(String property, Sort.Direction direction)"
        }
    }

//    public PageRequest toPageable() {
//        return PageRequest.of(page, size, Sort.by(direction, sortBy));
//    }

    public PageRequest toPageable() {
        if (sortOrders.isEmpty()) {
            // Usa ordenação simples para compatibilidade
            return org.springframework.data.domain.PageRequest.of(
                    page, size, Sort.by(direction, sortBy)
            );
        } else {
            // Usa ordenação múltipla
            List<Sort.Order> orders = sortOrders.stream()
                    .map(this::toSortOrder)
                    .toList();

            return org.springframework.data.domain.PageRequest.of(
                    page, size, Sort.by(orders)
            );
        }
    }

    private Sort.Order toSortOrder(SortOrder sortOrder) {
        Sort.Order order = new Sort.Order(
                sortOrder.getDirection(),
                sortOrder.getProperty()
        );

        if (Boolean.TRUE.equals(sortOrder.getIgnoreCase())) {
            order = order.ignoreCase();
        }

        // Configura tratamento de nulls
        switch (sortOrder.getNullHandling().toUpperCase()) {
            case "NULLS_FIRST":
                order = order.nullsFirst();
                break;
            case "NULLS_LAST":
                order = order.nullsLast();
                break;
            case "NATIVE":
            default:
                // Mantém default do banco
                break;
        }

        return order;
    }

    // Método auxiliar para adicionar ordenação
    public PaginationRequest addSort(String property, Sort.Direction direction) {
        this.sortOrders.add(new SortOrder(property, direction));
        return this;
    }

    public PaginationRequest addSort(String property) {
        return addSort(property, Sort.Direction.ASC);
    }
}