package io.factorialsystems.msscwallet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscwallet.config.JMSConfig;
import io.factorialsystems.msscwallet.dto.AuditMessageDto;
import io.factorialsystems.msscwallet.utils.Security;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    @Value("${spring.application.name}")
    private String serviceName;

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void auditEvent(String message, String action) {

        AuditMessageDto dto = AuditMessageDto
                .builder()
                .createdDate(new Date())
                .serviceAction(action)
                .serviceName(serviceName)
                .description(message)
                .userName(Security.getUserName())
                .build();

        try {
            jmsTemplate.convertAndSend(JMSConfig.AUDIT_MESSAGE_QUEUE, objectMapper.writeValueAsString(dto));
        } catch (Exception e) {
            log.error("Error Sending Audit Message reason : {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
