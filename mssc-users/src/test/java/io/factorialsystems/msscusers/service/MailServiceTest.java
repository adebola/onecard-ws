package io.factorialsystems.msscusers.service;

import io.factorialsystems.msscusers.dto.MailMessageDto;
import io.factorialsystems.msscusers.external.client.CommunicationClient;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@CommonsLog
@SpringBootTest
class MailServiceTest {

    @Autowired
    private CommunicationClient client;

    @Test
    void sendMailWithOutAttachment() {
        MailMessageDto dto = MailMessageDto.builder()
                .to("adeomoboya@gmail.com")
                .body("test")
                .subject("test")
                .build();

        client.sendMailWithoutAttachment(dto);
        log.info("Mail sent successfully");
    }
}