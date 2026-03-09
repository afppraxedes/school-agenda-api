package com.schoolagenda.domain.enums;

import lombok.Getter;

@Getter
public enum MessageType {
    COMUNICADO("comunicado"),
    MENSAGEM("mensagem"),
    AVISO("aviso");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }
}
