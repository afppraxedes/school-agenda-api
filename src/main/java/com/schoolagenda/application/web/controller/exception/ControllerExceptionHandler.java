package com.schoolagenda.application.web.controller.exception;

import com.schoolagenda.domain.exception.RefreshTokenExpired;
import com.schoolagenda.domain.exception.StandardError;
import com.schoolagenda.domain.exception.TokenRefreshException;
import com.schoolagenda.domain.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ControllerAdvice
public class ControllerExceptionHandler {

    // Exceção para autenticação (token) e refresh token, pois as duas classes "BadCredentialsException" e
    // "RefreshTokenExpired" extendem de "RuntimeException"!
    @ExceptionHandler({BadCredentialsException.class, RefreshTokenExpired.class})
    ResponseEntity<StandardError> handleBadCredentialsException(
            final RuntimeException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build());
    }

    // Exceção para validação de campos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<StandardError> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        // A excetion "ValidationException" é a que foi criada na "hd-commons-lib"!
        var error = ValidationException.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Exception")
                .message("Exception in validation attributes")
                .path(request.getRequestURI())
                .errors(new ArrayList<>())
                .build();

        // Para que mensagens de erros sejam lançadas para todos os campos
        for(FieldError fieldError: ex.getBindingResult().getFieldErrors()) {
            error.addErros(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(error);
    }

    // Exceção para refresh token não encontrado
    @ExceptionHandler(TokenRefreshException.class)
    ResponseEntity<StandardError> handleTokenRefreshException(
            final TokenRefreshException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                StandardError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message("Refresh token is not present in database!")
                .path(request.getRequestURI())
                .build());
    }

}
