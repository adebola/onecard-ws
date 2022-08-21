package io.factorialsystems.mssccommunication.service.mail;

import io.factorialsystems.mssccommunication.dto.MailMessageDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CommonsLog
@SpringBootTest
class MailServiceTest {
    @Autowired
    private MailService mailService;

    @Test
    void sendMail() {
        MailMessageDto dto = new MailMessageDto();
        dto.setTo("adeomoboya@gmail.com");
        dto.setBody("Test Message");
        dto.setSubject("Test");

        //mailService.sendMail(dto, null);
    }
}