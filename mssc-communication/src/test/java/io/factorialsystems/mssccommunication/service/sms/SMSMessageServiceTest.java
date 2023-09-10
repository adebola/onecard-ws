package io.factorialsystems.mssccommunication.service.sms;

import io.factorialsystems.mssccommunication.dto.AsyncSMSMessageDto;
import io.factorialsystems.mssccommunication.dto.SMSMessageDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@CommonsLog
@SpringBootTest
class SMSMessageServiceTest {

    @Autowired
    private SMSMessageService messageService;

    @Test
    void sendMessage() {
        SMSMessageDto dto = new SMSMessageDto();

        dto.setMessage("Hello hello helloxxxxxxxx");
        dto.setTo("2348055572307");
        //final SMSResponseDto smsResponseDto = messageService.sendMessage(dto);
        //log.info(smsResponseDto);
    }


    @Test
    void sendAsyncMessage() {
        AsyncSMSMessageDto dto = new AsyncSMSMessageDto();
        dto.setEmail("adeomoboya@gmail.com");
        dto.setUserId(UUID.randomUUID().toString());
        dto.setMessage("Hello World!");
        dto.setTo("08055572307");

        //messageService.asyncSendMessage(dto);
    }
}