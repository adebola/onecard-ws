package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dto.AuditMessageDto;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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

    @SneakyThrows
    public void auditEvent(String message, String action) {

        AuditMessageDto dto = AuditMessageDto
                .builder()
                .createdDate(new Date())
                .serviceAction(action)
                .serviceName(serviceName)
                .description(message)
                .userName(ProviderSecurity.getUserName())
                .build();

        jmsTemplate.convertAndSend(JMSConfig.AUDIT_MESSAGE_QUEUE, objectMapper.writeValueAsString(dto));
    }
}
