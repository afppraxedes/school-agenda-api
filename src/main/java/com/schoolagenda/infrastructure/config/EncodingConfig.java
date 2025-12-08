package com.schoolagenda.infrastructure.config;

import org.springframework.context.annotation.Configuration;

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