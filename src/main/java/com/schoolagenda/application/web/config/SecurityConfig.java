package com.schoolagenda.application.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.schoolagenda.domain.repository.UserRepository;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String[] SWAGGER_WHITELIST = {"/swagger-ui/index.html", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**", "/h2-console/**"};

    // Responsáel por "setar" um filtro
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // "/api/auth/login/**" --> utilizar para os testes caso dê algum problema!
                // TODO: FORMA DE HABILITAR O "H2 CONSOLE" APÓS IMPLEMENTAR A SEGURANÇA NO SISTEMA
////                // INÍCIO
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
////                // FIM
//                .csrf(csrf -> csrf.disable())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(http -> http
                        .requestMatchers("/api/auth/**").permitAll() // TODO: remover após os testes!
//                        .requestMatchers("/api/director/**").hasAuthority("DIRECTOR")
//                        .requestMatchers("/api/teacher/**").hasAnyAuthority("TEACHER", "DIRECTOR")
//                        .requestMatchers("/api/responsible/**").hasAnyAuthority("RESPONSIBLE", "DIRECTOR")
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


// TODO: IMPLEMENTAÇÃO DE SEGURANÇA ANTERIOR (BASIC SECURITY). POSSUI "URL's" DE PERMISSÃO DO "CORS" IMPORTANTES"!
//    private final UserRepository userRepository;
//
//    // Use UserRepository em vez de UserService para quebrar o ciclo
//    public SecurityConfig(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                // TODO: FORMA DE HABILITAR O "H2 CONSOLE" APÓS IMPLEMENTAR A SEGURANÇA NO SISTEMA
////                // INÍCIO
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
//                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
////                // FIM
//                .csrf(csrf -> csrf.disable())
////                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/director/**").hasAuthority("DIRECTOR")
//                        .requestMatchers("/api/teacher/**").hasAnyAuthority("TEACHER", "DIRECTOR")
//                        .requestMatchers("/api/responsible/**").hasAnyAuthority("RESPONSIBLE", "DIRECTOR")
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(httpBasic -> {});
//
//        return http.build();
//    }
//
////    @Bean
////    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        http
////                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
////                // TODO: FORMA DE HABILITAR O "H2 CONSOLE" APÓS IMPLEMENTAR A SEGURANÇA NO SISTEMA
////                // INÍCIO
////                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
////                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
////                // FIM
////                .csrf(csrf -> csrf.disable())
////                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////                .authorizeHttpRequests(authz -> authz
////                        .requestMatchers("/api/auth/**").permitAll()
////                        .requestMatchers("/api/director/**").hasAuthority("DIRECTOR")
////                        .requestMatchers("/api/teacher/**").hasAnyAuthority("TEACHER", "DIRECTOR")
////                        .requestMatchers("/api/responsible/**").hasAnyAuthority("RESPONSIBLE", "DIRECTOR")
////                        .anyRequest().authenticated()
////                )
////                .httpBasic(httpBasic -> {});
////
////        return http.build();
////    }
//
////    @Bean
////    public CorsConfigurationSource corsConfigurationSource() {
////        CorsConfiguration configuration = new CorsConfiguration();
////        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
////        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
////        configuration.setAllowedHeaders(Arrays.asList("*"));
////        configuration.setAllowCredentials(true);
////
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        source.registerCorsConfiguration("/**", configuration);
////        return source;
////    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // ✅ CORRETO: Adicionar TODAS as URLs em uma única lista
//        configuration.setAllowedOrigins(Arrays.asList(
//                "http://localhost:3000",
//                "http://192.168.1.5:3000",
//                "http://localhost:4200",
//                "http://192.168.1.5:4200",
//                "http://172.28.192.1:4200",
//                "https://unlacquered-omnivorous-stacie.ngrok-free.dev",
//                "https://*.ngrok-free.dev" // Permite todos os subdomínios ngrok
//        ));
//
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
//        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
//        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L); // Cache por 1 hora
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService() {
//        return username -> userRepository.findByUsername(username)
//                .map(user -> new org.springframework.security.core.userdetails.User(
//                        user.getUsername(),
//                        user.getPassword(),
//                        user.getRoles().stream()
//                                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.name()))
//                                .toList()
//                ))
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//    }
}