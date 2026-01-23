package com.schoolagenda.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "utcDateTimeProvider")// Nome do bean
public class JpaAuditingConfig {

    // Opcional: se quiser definir explicitamente
//    @Bean
//    public AuditorAware<String> auditorAware() {
//        return new AuditorAwareImpl();
//    }

    @Bean
    public DateTimeProvider utcDateTimeProvider() {
        // Força o Spring Auditor a usar OffsetDateTime em UTC
        return () -> Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
    }
}

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.auditing.DateTimeProvider;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.util.Optional;
//
//@Configuration
//@EnableJpaAuditing(dateTimeProviderRef = "utcDateTimeProvider")
//public class JpaAuditingConfig {
//
//    @Bean
//    public DateTimeProvider utcDateTimeProvider() {
//        // Força o Spring Auditor a usar OffsetDateTime em UTC
//        return () -> Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
//    }
//}