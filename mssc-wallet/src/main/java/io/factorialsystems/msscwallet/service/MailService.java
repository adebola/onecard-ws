package io.factorialsystems.msscwallet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscwallet.config.JMSConfig;
import io.factorialsystems.msscwallet.dto.MailMessageDto;
import io.factorialsystems.msscwallet.external.client.MailClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final MailClient mailClient;
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendMailWithOutAttachment(MailMessageDto dto) {
        log.info(String.format("Sending Mail without attachment to %s", dto.getTo()));
        mailClient.sendMailWithoutAttachment(dto);
    }

    @SneakyThrows
    public void pushMailMessage(MailMessageDto dto) {
        jmsTemplate.convertAndSend(JMSConfig.SEND_MAIL_QUEUE, objectMapper.writeValueAsString(dto));
    }
}
