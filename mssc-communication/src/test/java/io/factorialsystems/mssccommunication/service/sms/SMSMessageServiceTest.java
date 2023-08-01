package io.factorialsystems.mssccommunication.service.sms;

import io.factorialsystems.mssccommunication.dto.SMSMessageDto;
import io.factorialsystems.mssccommunication.dto.SMSResponseDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@CommonsLog
@SpringBootTest
class SMSMessageServiceTest {

    @Autowired
    private SMSMessageService messageService;

    @Test
    void sendMessage() {
        SMSMessageDto dto = new SMSMessageDto();

        dto.setMessage("Madam mo ko yin je gan, let me change your live N10m NOW NOW");
        dto.setTo("2348188111333");
        final SMSResponseDto smsResponseDto = messageService.sendMessage(dto);
        log.info(smsResponseDto);
    }
}