package com.schoolagenda.infrastructure.config;

import com.schoolagenda.domain.service.WebPushService;
import com.schoolagenda.infrastructure.external.webpush.WebPushServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebPushConfig {

    @Value("${webpush.vapid.public-key:BFmYrl81x7ilZXaacrFEWT_clB9N_VH_pK2aILAxNhvjgL0Nlq14V0xKPageaSk2c_S8Ea4y-T1PStXpIHUgZEM}")
    private String vapidPublicKey;

    @Value("${webpush.vapid.private-key:COaG595od-XdZZ6fgW-K7e5_heYdgxnzJ2COlbcohHQ}")
    private String vapidPrivateKey;

    @Value("${webpush.vapid.subject:mailto:admin@schoolagenda.com}")
    private String vapidSubject;

    @Bean
    public WebPushService webPushService() {
        logger.info("🔧 Creating WebPushService bean...");
        logger.info("🔑 Public Key: {}...", vapidPublicKey.substring(0, Math.min(20, vapidPublicKey.length())));
        logger.info("📧 Subject: {}", vapidSubject);

        return new WebPushServiceImpl(vapidPublicKey, vapidPrivateKey, vapidSubject);
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WebPushConfig.class);
}