package com.schoolagenda.application.web.util;


import com.schoolagenda.application.web.security.dtos.UserDetailsDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

// Responsável pela geração do token
@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(final UserDetailsDTO detailsDTO) {
        // Construindo um token
        return Jwts.builder()
                // Atributos que estarão no token
                .claim("id", detailsDTO.getId())
                .claim("name", detailsDTO.getName())
                .claim("authorities", detailsDTO.getAuthorities())
                // O email fica dentro do "subjects
                .setSubject(detailsDTO.getUsername())
                // Setando a "assinatura" do token. Deve ser uma sequência de "bytes" em função da "assinatura do algoritmo,
                // que é "HS512"!
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }

}
