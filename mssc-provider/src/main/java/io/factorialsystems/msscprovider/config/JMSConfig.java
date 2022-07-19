package io.factorialsystems.msscprovider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JMSConfig {
    public static final String AUDIT_MESSAGE_QUEUE = "audit-message-queue";
    public static final String NEW_TRANSACTION_QUEUE = "new-transaction-queue";
    public static final String SINGLE_RECHARGE_QUEUE = "single-recharge-queue";
    public static final String NEW_BULK_RECHARGE_QUEUE = "new-bulk-recharge-queue";
    public static final String SEND_PROVIDER_MAIL_QUEUE = "communication-mail-without-attachment-queue";
    public static final String PAYMENT_REFUND_QUEUE = "payment-refund-queue";
    public static final String WALLET_REFUND_RESPONSE_QUEUE_PROVIDER = "wallet-refund-response-queue-provider";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
