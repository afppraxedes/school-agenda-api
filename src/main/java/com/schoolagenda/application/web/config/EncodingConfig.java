package com.schoolagenda.application.web.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;
import java.util.TimeZone;

@Configuration
public class EncodingConfig {

//    @Bean
//    public Filter characterEncodingFilter() {
//        CharacterEncodingFilter filter = new CharacterEncodingFilter();
//        filter.setEncoding("UTF-8");
//        filter.setForceEncoding(true);
//        return filter;
//    }
//
//    @Bean
//    public HttpMessageConverter<String> responseBodyConverter() {
//        StringHttpMessageConverter converter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
//        converter.setWriteAcceptCharset(false);
//        return converter;
//    }
//
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
//        return builder -> {
//            builder.simpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//            builder.timeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
//            builder.modules(new JavaTimeModule());
//            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        };
//    }
}