package io.factorialsystems.mssccommunication.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.mssccommunication.config.JMSConfig;
import io.factorialsystems.mssccommunication.dto.AsyncSMSMessageDto;
import io.factorialsystems.mssccommunication.dto.MailMessageDto;
import io.factorialsystems.mssccommunication.service.mail.MailService;
import io.factorialsystems.mssccommunication.service.sms.SMSMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunicationListener {
    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final SMSMessageService smsMessageService;

    @JmsListener(destination = JMSConfig.SEND_PROVIDER_MAIL_QUEUE)
    public void listenForMailWithoutAttachment(String jsonData) throws IOException {

        if (jsonData != null) {
            MailMessageDto mailMessageDto = objectMapper.readValue(jsonData, MailMessageDto.class);
            log.info(String.format("Received Asynchronous Request to send Mail %s", mailMessageDto.getTo()));
            mailService.sendMail(mailMessageDto, null);
        }
    }

    @JmsListener(destination = JMSConfig.SEND_SMS_QUEUE)
    public void listenForSMS(String jsonData) throws IOException {
        if (jsonData != null) {
            AsyncSMSMessageDto smsMessageDto = objectMapper.readValue(jsonData, AsyncSMSMessageDto.class);
            log.info(String.format("Received Asynchronous Request to send SMS to %s", smsMessageDto.getTo()));
            smsMessageService.asyncSendMessage(smsMessageDto);
        }
    }
}
