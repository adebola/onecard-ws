package io.factorialsystems.msscreports.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscreports.config.JMSConfig;
import io.factorialsystems.msscreports.dto.AuditMessageDto;
import io.factorialsystems.msscreports.utils.K;
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
                .userName(K.getUserName())
                .build();

        jmsTemplate.convertAndSend(JMSConfig.AUDIT_MESSAGE_QUEUE, objectMapper.writeValueAsString(dto));
    }
}
