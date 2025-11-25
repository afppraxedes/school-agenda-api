//package com.schoolagenda.application.web.security;
//
//import com.schoolagenda.application.web.util.JwtService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            final String jwt = authHeader.substring(7);
//            final String userEmail = jwtService.extractUsername(jwt);
//
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (userEmail != null && authentication == null) {
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//
//                if (jwtService.isTokenValid(jwt, userDetails)) {
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );
//
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//                }
//            }
//        } catch (Exception e) {
//            logger.error(String.format("Cannot set user authentication: {}", e.getMessage()));
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}


// TODO: SEM OS "LOGS"
//package com.schoolagenda.application.web.security;
//
//import com.schoolagenda.application.web.util.JwtService;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.lang.NonNull;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//// ✅ REMOVA @Component - Agora é gerenciado manualmente pela SecurityConfig
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//
//        // Skip filter if no Bearer token
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            final String jwt = authHeader.substring(7);
//            final String userEmail = jwtService.extractUsername(jwt);
//
//            // Only authenticate if no existing authentication
//            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//
//                if (jwtService.isTokenValid(jwt, userDetails)) {
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );
//
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//
//                    logger.info("Authenticated user: " + userEmail);
//                }
//            }
//        } catch (Exception e) {
//            logger.error(String.format("Cannot set user authentication: {}", e.getMessage()));
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
// FIM - SEM OS "LOGS"

package com.schoolagenda.application.web.security;

import com.schoolagenda.application.web.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService; // ✅ Mesmo serviço
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);

            // ✅ USAR JwtService PARA VALIDAR E EXTRAIR
            if (jwtService.validateToken(jwt)) {
                String username = jwtService.extractUsername(jwt);

                if (username != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }
}

//@Slf4j
//@RequiredArgsConstructor
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        final String authHeader = request.getHeader("Authorization");
//        final String requestURI = request.getRequestURI();
//
//        log.info("🔐 JWT Filter - Request: {} {}", request.getMethod(), requestURI);
//
//        // Skip filter if no Bearer token
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            log.warn("❌ No Bearer token found for protected endpoint: {}", requestURI);
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            final String jwt = authHeader.substring(7);
//            log.info("🔐 JWT Token received (first 20 chars): {}...", jwt.substring(0, Math.min(20, jwt.length())));
//
//            final String userEmail = jwtService.extractUsername(jwt);
//            log.info("🔐 Extracted username from token: {}", userEmail);
//
//            if (userEmail == null) {
//                log.error("❌ Could not extract username from token");
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            // Only authenticate if no existing authentication
//            if (SecurityContextHolder.getContext().getAuthentication() == null) {
//                log.info("🔐 Loading user details for: {}", userEmail);
//
//                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//                log.info("🔐 User details loaded: {}", userDetails.getUsername());
//
//                log.info("🔐 Validating token...");
//                boolean isValid = jwtService.isTokenValid(jwt, userDetails);
//                log.info("🔐 Token valid: {}", isValid);
//
//                if (isValid) {
//                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                            userDetails,
//                            null,
//                            userDetails.getAuthorities()
//                    );
//
//                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authToken);
//
//                    log.info("✅ Successfully authenticated user: {} with authorities: {}",
//                            userEmail, userDetails.getAuthorities());
//                } else {
//                    log.error("❌ Token validation failed for user: {}", userEmail);
//                }
//            } else {
//                log.info("⏩ User already authenticated, skipping");
//            }
//        } catch (Exception e) {
//            log.error("💥 ERROR in JWT Filter: {}", e.getMessage(), e);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}