package com.schoolagenda.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
//import nonapi.io.github.classgraph.json.Id;

import java.time.LocalDateTime;

// Responsável por ser nossa documentação do "Refresh Token"
@Builder
@Getter
@Entity
public class RefreshToken {

    // NOTA: o "id" será o prórpio "token"
    @Id
    private String id;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

}
