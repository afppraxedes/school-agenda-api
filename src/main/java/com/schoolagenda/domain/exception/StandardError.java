package com.schoolagenda.domain.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
// A anotação "SuperBuilder" é para que os atributos sejam acessíveis pela classe que
// está extendendo "StandardError" com o builder também.
@SuperBuilder
public class StandardError {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

}
