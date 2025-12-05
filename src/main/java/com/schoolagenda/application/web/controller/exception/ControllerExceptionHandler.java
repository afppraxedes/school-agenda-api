package com.schoolagenda.application.web.controller.exception;

import com.schoolagenda.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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

    // Exceção para campos que não possuam valor
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<StandardError> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message("JSON parse error: All fields must have a valid format")
                        .path(request.getRequestURI())
                        .build());
    }

    // Exceção para parâmetros com formatos (tipos de dados) inválidos
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<StandardError> handleMethodArgumentTypeMismatchException(
            final MethodArgumentTypeMismatchException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message("The parameter must have a valid format")
                        .path(request.getRequestURI())
                        .build());
    }

    // Exceção para parâmetros com formatos (tipos de dados) inválidos
    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<StandardError> handleNoResourceFoundException(
            final NoResourceFoundException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build());
    }

    // Exceção para campos que não possuam valor
    @ExceptionHandler(DuplicateResourceException.class)
    ResponseEntity<StandardError> handleDuplicateResourceException(
            final DuplicateResourceException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Field validation exception.")
                        .message("A user is already registered")
                        .path(request.getRequestURI())
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<StandardError> handleResourceNotFoundException(
            final ResourceNotFoundException ex, final HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                StandardError.builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .build());
    }

    // TODO: já possui um método acima para "recurso não encontrado". Verificar se deixo esse abaixo, pois está apenas
    // para o "refresh token"
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
