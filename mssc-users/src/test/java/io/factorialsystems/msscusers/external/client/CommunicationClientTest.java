package io.factorialsystems.msscusers.external.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class CommunicationClientTest {
    @Autowired
    CommunicationClient communicationClient;

    @Test
    void sendMailWithoutAttachment() {
//        MailMessageDto mailMessageDto = MailMessageDto.builder()
//                .body("Test E-Mail")
//                .to("adeomoboya@gmail.com")
//                .subject("Test")
//                .secret("secret")
//                .build();
//
//        final String s = communicationClient.sendMailWithoutAttachment(mailMessageDto);
//        log.info(s);
    }

    @Test
    void uploadFile() {
    }
}