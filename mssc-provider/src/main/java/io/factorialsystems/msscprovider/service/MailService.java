package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dto.MailMessageDto;
import io.factorialsystems.msscprovider.external.client.CommunicationClient;
import io.factorialsystems.msscprovider.external.client.CommunicationJSONClient;
import io.factorialsystems.msscprovider.service.file.CustomMultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final CommunicationClient communicationClient;
    private final CommunicationJSONClient communicationJSONClient;

    public String sendMailWithOutAttachment(MailMessageDto dto) {
        log.info(String.format("Sending Mail without attachment to %s", dto.getTo()));
        return communicationJSONClient.sendMailWithoutAttachment(dto);
    }

    @SneakyThrows
    public void pushMailWithOutAttachment(MailMessageDto dto) {
        jmsTemplate.convertAndSend(JMSConfig.SEND_PROVIDER_MAIL_QUEUE, objectMapper.writeValueAsString(dto));
    }

    public String sendMailWithAttachment(File file, MailMessageDto dto, String name, String contentType)  {
        try (FileInputStream input = new FileInputStream(file)) {
            MultipartFile multipartFile = new CustomMultipartFile(IOUtils.toByteArray(input), name, contentType, file.getName());
            return communicationClient.sendMailWithAttachment(dto, multipartFile);
        } catch (IOException e) {
            log.error("error sending mail with attachment : {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
