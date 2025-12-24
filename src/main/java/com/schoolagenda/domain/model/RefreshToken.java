//package com.schoolagenda.domain.model;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
////import nonapi.io.github.classgraph.json.Id;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//
//// Responsável por ser nossa documentação do "Refresh Token"
//@Builder
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//public class RefreshToken {
//
//    // NOTA: o "id" será o prórpio "token"
//    @Id
//    private String id;
//    private String username;
//    private LocalDateTime createdAt;
//    private LocalDateTime expiresAt;
//
//    // TODO: OS CAMPOS ABAIXO E O MÉTODO "isActive" SÃO DA SUGESTÃO DE MELHORIAS DO "DEEPSEEK". REFAZER AS
//    // MELHORIAS PARA UTILIZAR OS CAMPOS ABAIXO!
//    // NOVOS CAMPOS DE SEGURANÇA
////    @Column(nullable = false)
////    private String userAgent;
////
////    @Column(nullable = false)
////    private String ipAddress;
////
////    @Column(nullable = false)
////    private boolean revoked = false;
//
//    // ✅ Método para verificar se está ativo
////    public boolean isActive() {
////        return !revoked && expiresAt.isAfter(LocalDateTime.now());
////    }
//
//}

package com.schoolagenda.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;
}
