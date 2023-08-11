package io.factorialsystems.mssccommunication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JMSConfig {
    public static final String SEND_PROVIDER_MAIL_QUEUE = "communication-mail-without-attachment-queue";
    public static final String SEND_SMS_QUEUE = "communication-sms-queue";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
