package com.schoolagenda.application.web.util;

// Execute este código uma vez para gerar uma chave
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

public class GenerateSecret {
    public static void main(String[] args) {
        // Gerar uma chave segura para HS512
        byte[] keyBytes = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512).getEncoded();
        String base64Secret = Base64.getEncoder().encodeToString(keyBytes);

        System.out.println("✅ Your new JWT Secret:");
        System.out.println("jwt.secret=" + base64Secret);
        System.out.println("🔑 Key length: " + base64Secret.length() + " characters");
    }
}