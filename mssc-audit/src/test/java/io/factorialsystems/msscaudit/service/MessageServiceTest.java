package io.factorialsystems.msscaudit.service;

import io.factorialsystems.msscaudit.dto.AuditMessageDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@CommonsLog
class MessageServiceTest {

    @Autowired
    MessageService service;

    @Test
    void save() {
        AuditMessageDto dto = new AuditMessageDto();
        dto.setServiceAction("TEST");
        dto.setServiceName("test-service");
        dto.setCreatedDate(new Date());
        dto.setUserName("adebola");
        dto.setDescription("Description");

        service.save(dto);
    }

    @Test
    void findAll() {
        var x = service.findAll(0, 20);
        assertNotNull(x);
        assert(x.getList().size() > 0);
        log.info(x);
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
