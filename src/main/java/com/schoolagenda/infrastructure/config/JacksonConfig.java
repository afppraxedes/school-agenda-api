package com.schoolagenda.infrastructure.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;

// BigDecimalDeserializer customizado, para quiser manter compatibilidade com APIs existentes
@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule bigDecimalModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
        return module;
    }

    public static class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException {
            String value = p.getText();
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            try {
                return new BigDecimal(value).setScale(2, java.math.RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                throw new IOException("Valor numérico inválido: " + value, e);
            }
        }
    }
}