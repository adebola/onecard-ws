package io.factorialsystems.msscaudit.service;

import io.factorialsystems.msscaudit.dto.AuditMessageDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@CommonsLog
class MessageServiceTest {

    @Autowired
    MessageService service;

    @Test
    void save() {
        AuditMessageDto dto = AuditMessageDto
                .builder()
                .serviceAction("TEST")
                .serviceName("test-service")
                .createdDate(new Date())
                .userName("adebola")
                .description("Description")
                .build();

        service.save(dto);
    }

    @Test
    void findAll() {
        service.findAll(0, 20);
    }

    @Test
    void findById() {
//        String id = "617fe9e5a8f9376defa90533";
//
//        AuditMessageDto dto = service.findById(id);
//        assertEquals(id, dto.getId());
//        log.info(dto);
    }
}
