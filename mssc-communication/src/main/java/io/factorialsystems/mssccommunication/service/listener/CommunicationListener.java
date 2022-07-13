package io.factorialsystems.mssccommunication.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.mssccommunication.config.JMSConfig;
import io.factorialsystems.mssccommunication.dto.MailMessageDto;
import io.factorialsystems.mssccommunication.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationListener {
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    @JmsListener(destination = JMSConfig.SEND_PROVIDER_MAIL_QUEUE)
    public void listenForMailWithoutAttachment(String jsonData) throws IOException {

        if (jsonData != null) {
            MailMessageDto mailMessageDto = objectMapper.readValue(jsonData, MailMessageDto.class);

            log.info(String.format("Received Asynchronous Request to send Mail %s", mailMessageDto.getTo()));

            MailService mailService = applicationContext.getBean(MailService.class);
            mailService.sendMail(mailMessageDto, null);
        }
    }
}
