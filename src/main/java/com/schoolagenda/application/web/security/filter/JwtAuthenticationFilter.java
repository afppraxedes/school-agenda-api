package com.schoolagenda.application.web.security.filter;

import com.schoolagenda.application.web.security.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String requestPath = request.getServletPath();

        log.debug("Processing request to: {}", requestPath);

        // ⭐⭐ MUDANÇA CRÍTICA: Verifique se é rota pública PRIMEIRO ⭐⭐
        if (isPublicEndpoint(requestPath)) {
            log.debug("Public endpoint, skipping JWT filter: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // ⭐⭐ Só verifica token se NÃO for endpoint público ⭐⭐
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("❌ No Bearer token found for SECURED endpoint: {}", requestPath);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
            return; // ⬅️ NÃO chama filterChain.doFilter() para endpoints protegidos sem token
        }

        try {
//            final String jwt = authHeader.substring(7);
//
//            if (!jwtService.validateToken(jwt)) {
//                log.error("Token validation failed for path: {}", requestPath);
//                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
//                return;
//            }

            // 1. Extração com limpeza de espaços
            final String jwt = authHeader.substring(7).trim();

            // DEBUG CRÍTICO: Veja no console o que realmente foi extraído
            // Se aparecer um UUID aqui, você está enviando o token errado no Postman
            log.debug("Extracted JWT for validation: [{}]", jwt.length() > 10 ? jwt.substring(0, 10) + "..." : jwt);

            if (jwt.isEmpty()) {
                log.error("❌ JWT string is empty after 'Bearer ' prefix");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is empty");
                return;
            }

            if (!jwtService.validateToken(jwt)) {
                log.error("❌ Token validation failed for path: {}", requestPath);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            String username = jwtService.extractUsername(jwt);

            if (username == null || username.isEmpty()) {
                log.error("Username is null or empty");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token claims");
                return;
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.info("✅ Authentication set for user: {}", username);
            }

        } catch (Exception e) {
            log.error("❌ Error setting authentication for path: {}", requestPath, e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication error");
            return;
        }

        filterChain.doFilter(request, response);
    }

    // ⭐⭐ MÉTODO AUXILIAR PARA VERIFICAR ROTAS PÚBLICAS ⭐⭐
    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/v1/auth/") ||
                path.startsWith("/api/auth/") ||       // ⬅️ ADICIONE ESTA LINHA!
                path.startsWith("/h2-console/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/api-docs") ||
                path.startsWith("/swagger-resources") ||
                path.startsWith("/webjars/") ||
                path.equals("/swagger-ui.html") ||
                path.equals("/favicon.ico") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/actuator/info") ||
                path.equals("/error");
    }

    // ⭐⭐ REMOVA o método shouldNotFilter() ⭐⭐
    // @Override
    // protected boolean shouldNotFilter(HttpServletRequest request) {
    //     String path = request.getServletPath();
    //     return isPublicEndpoint(path);
    // }
}