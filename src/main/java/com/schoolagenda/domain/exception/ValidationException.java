package com.schoolagenda.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

// Responsável pela mensagens de validação de campos
@SuperBuilder
public class ValidationException extends StandardError {

    @Getter
    private List<FieldError> errors;

    @Getter
    @AllArgsConstructor
    private static class FieldError {
        private String fieldName;
        private String message;
    }

    public void addErros(final String fieldName, final String message) {
        this.errors.add(new FieldError(fieldName, message));
    }

}
