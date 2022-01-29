package io.factorialsystems.msscprovider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JMSConfig {
    public static final String NEW_USER_QUEUE = "UserQueue";
    public static final String UPDATE_USER_WALLET_QUEUE = "update-user-wallet";
    public static final String NEW_USER_WALLET_QUEUE = "new-user-wallet";
    public static final String AUDIT_MESSAGE_QUEUE = "audit-message-queue";
    public static final String NEW_TRANSACTION_QUEUE = "new-transaction-queue";
    public static final String BULK_RECHARGE_QUEUE = "bulk-recharge-queue";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
