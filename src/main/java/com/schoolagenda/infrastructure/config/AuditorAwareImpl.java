package com.schoolagenda.infrastructure.config;

// TODO: VERIFICAR ESTAS SUGESTÕES DE PACOTE E AJUSTAR SE HOUVER NECESSIDADE!
//package com.schoolagenda.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of("SYSTEM_AUTH_NULL");
        }

        // **DEBUG MELHORADO** (Manter no código temporariamente)
         System.out.println("=== DEBUG AUDITORIA (AJUSTADO) ===");
         System.out.println("Classe de autenticação: " + authentication.getClass().getName());
         System.out.println("Principal class: " + authentication.getPrincipal().getClass().getName());
         System.out.println("=== FIM DEBUG ===");


        // 1. Verificar se é uma autenticação anônima (mesmo que Authenticated=true)
        if (authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of("SYSTEM_ANONYMOUS");
        }

        // 2. Tentar buscar o Username (Email) do seu objeto customizado
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            // Seu objeto UserDetailsDTO (AgendaUserDetails) implementa UserDetails
            return Optional.of(((UserDetails) principal).getUsername());
        }

        // 3. Fallback se o principal for apenas o Username (String)
        if (principal instanceof String) {
            return Optional.of((String) principal);
        }

        return Optional.of("SYSTEM_UNKNOWN");
    }
}

//@Component
//public class AuditorAwareImpl implements AuditorAware<String> {
//
//    @Override
//    public Optional<String> getCurrentAuditor() {
////        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
////
////        System.out.println("authentication: " + authentication +
////                "\n!authentication.isAuthenticated(): "  + !authentication.isAuthenticated() +
////                "\nuthentication.getPrincipal().equals(\"anonymousUser\")" + authentication.getPrincipal().equals("anonymousUser"));
////
////        if (authentication == null || !authentication.isAuthenticated() ||
////                // Ignora o token de autenticação padrão 'anonymousUser'
////                authentication.getPrincipal().equals("anonymousUser")) {
////            return Optional.of("SYSTEM");
////        }
////
////        Object principal = authentication.getPrincipal();
////        System.out.println("Current Auditor: " + authentication.getPrincipal());
////
////        // 1. Verifica se o principal é a nossa classe customizada
////        if (principal instanceof AgendaUserDetails) {
////            // 2. Retorna o email (que é o getUsername() na sua implementação)
////            return Optional.of(((AgendaUserDetails) principal).getEmail());
////        }
////
////        // Se por algum motivo não for nosso objeto, retorna o nome padrão
////        return Optional.of(authentication.getName());
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        System.out.println("=== DEBUG AUDITORIA ===");
//        System.out.println("Classe de autenticação: " + (authentication != null ? authentication.getClass().getName() : "null"));
//        System.out.println("Authentication object: " + authentication);
//        System.out.println("Is authenticated: " + (authentication != null ? authentication.isAuthenticated() : "null"));
//        System.out.println("Principal: " + (authentication != null ? authentication.getPrincipal() : "null"));
//        System.out.println("Principal class: " + (authentication != null && authentication.getPrincipal() != null ?
//                authentication.getPrincipal().getClass().getName() : "null"));
//        System.out.println("Authorities: " + (authentication != null ? authentication.getAuthorities() : "null"));
//        System.out.println("=== FIM DEBUG ===");
//
//        if (authentication == null || !authentication.isAuthenticated() ||
//                "anonymousUser".equals(authentication.getPrincipal())) {
//            System.out.println("Retornando SYSTEM - autenticação anônima ou nula");
//            return Optional.of("SYSTEM");
//        }
//
//        Object principal = authentication.getPrincipal();
//
//        if (principal instanceof AgendaUserDetails) {
//            String email = ((AgendaUserDetails) principal).getEmail();
//            System.out.println("Retornando email do usuário: " + email);
//            return Optional.of(email);
//        }
//
//        // Se for uma String (nome de usuário)
//        if (principal instanceof String) {
//            System.out.println("Retornando nome de usuário como String: " + principal);
//            return Optional.of((String) principal);
//        }
//
//        System.out.println("Retornando authentication.getName(): " + authentication.getName());
//        return Optional.of(authentication.getName());
//    }
//}

// TODO: VERIFICAR ESTAS SUGESTÕES DE PACOTE E AJUSTAR SE HOUVER NECESSIDADE!
//package com.schoolagenda.agenda.api.config;

// TODO; APÓS TESTAR A IMPLEMENTAÇÃO, REMOVER AS INSTRUÇÕES ABAIXO:
// TODO: VERIFICAR ESTAS SUGESTÕES DE PACOTE E AJUSTAR SE HOUVER NECESSIDADE!
//import com.schoolagenda.agenda.api.security.AgendaUserDetails; // Use seu objeto UserDetails


// IMPLEMENTAÇÃO ANTERIOR DO "GEMINI"
//import com.schoolagenda.application.web.security.dto.AgendaUserDetails;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Component("auditorAwareImpl")
//public class AuditorAwareImpl implements AuditorAware<String> {
//
//    @Override
//    public Optional<String> getCurrentAuditor() {
//        // 3. Obtém o objeto de autenticação do contexto de segurança
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            // Se não houver usuário autenticado (ex: durante inicialização/teste sem login)
//            return Optional.of("SYSTEM");
//        }
//
//        // Se você estiver usando um objeto UserDetails customizado:
//        Object principal = authentication.getPrincipal();
//
//        if (principal instanceof AgendaUserDetails /*AgendaUserDetails*/) {
//            // Retorna o username ou o ID do usuário (username é mais legível para auditoria)
//            return Optional.of(((AgendaUserDetails /*AgendaUserDetails*/) principal).getUsername());
//        }
//
//        // Caso o principal seja apenas uma String (ex: login sem UserDetails customizado)
//        return Optional.of(authentication.getName());
//    }
//}
