package io.factorialsystems.mssccommunication.service.sms;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CommonsLog
@SpringBootTest
class SMSServiceTest {

    @Autowired
    SMSMessageService service;

    @Test
    void sendMessage() {
//        SMSMessageDto dto = new SMSMessageDto();
//        dto.setTo("08055572307");
//        dto.setMessage("Jesus Is Lord");
//        dto.setCreatedDate(new Date());
//        service.sendMessage(dto);
    }
}