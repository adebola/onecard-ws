package io.factorialsystems.msscaudit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscaudit.config.JMSConfig;
import io.factorialsystems.msscaudit.dto.AuditMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageListener {
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    @JmsListener(destination = JMSConfig.AUDIT_MESSAGE_QUEUE)
    public void listenForAuditMessage(String jsonData) throws IOException {

        if (jsonData == null) {
            log.error("Audit Message Error, jsonData is null");
            return;
        }

        AuditMessageDto dto = objectMapper.readValue(jsonData, AuditMessageDto.class);

        if (dto != null) {
            MessageService service = applicationContext.getBean(MessageService.class);
            service.save(dto);
        }
    }
}
