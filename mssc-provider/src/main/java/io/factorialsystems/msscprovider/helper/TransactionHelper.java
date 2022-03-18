package io.factorialsystems.msscprovider.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.ApplicationContextProvider;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dto.RequestTransactionDto;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Builder
public class TransactionHelper {
    private Integer serviceId;
    private String requestId;
    private BigDecimal amount;
    private String userId;
    private String recipient;

    public void saveTransaction() {

        RequestTransactionDto requestTransactionDto = RequestTransactionDto.builder()
                .serviceId(serviceId)
                .requestId(requestId)
                .serviceCost(amount)
                .transactionDate(new Date().toString())
                .userId(userId)
                .recipient(recipient == null ? "scheduled-bulk" : "scheduled-" + recipient)
                .build();

        JmsTemplate jmsTemplate = ApplicationContextProvider.getBean(JmsTemplate.class);
        ObjectMapper objectMapper = ApplicationContextProvider.getBean(ObjectMapper.class);

        try {
            jmsTemplate.convertAndSend(JMSConfig.NEW_TRANSACTION_QUEUE, objectMapper.writeValueAsString(requestTransactionDto));
        } catch (JsonProcessingException e) {
            log.error("Error sending JMS Transaction Message to Wallet service {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
