package io.factorialsystems.msscaudit.service;

import io.factorialsystems.msscaudit.dto.AuditMessageDto;
import io.factorialsystems.msscaudit.dto.AuditSearchDto;
import io.factorialsystems.msscaudit.dto.PagedDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void findAll_UnPaged() {
        var x = service.findAllUnPaged(null);
        log.info(x.size());
        log.info(x);
    }

    @Test
    void findAll_UnPaged_StartDateOnly() {
        Instant start = Instant.parse("2022-02-15T18:35:24.00Z");
        Instant end =  Instant.parse("2022-03-15T18:35:24.00Z");
        AuditSearchDto dto = new AuditSearchDto(null, start, end);
        var x = service.findAllUnPaged(dto);
        log.info(x.size());
    }



    @Test
    void search_serviceAction() {
        AuditSearchDto auditSearchDto = new AuditSearchDto("Account", null, null);
        final PagedDto<AuditMessageDto> search = service.search(1, 20, auditSearchDto);
        log.info(search);
    }

    @Test
    void search_startDate_endDate() throws ParseException {

        Instant start = Instant.parse("2022-02-15T18:35:24.00Z");
        Instant end =  Instant.parse("2022-03-15T18:35:24.00Z");

        AuditSearchDto auditSearchDto = new AuditSearchDto(null, start, end);
        final PagedDto<AuditMessageDto> search = service.search(1, 20, auditSearchDto);
        log.info(search);
    }

    @Test
    void findById() {
       final String id = "61d05e145fb6ab623be3fbc5";

        AuditMessageDto dto = service.findById(id);
        assertEquals(id, dto.getId());
        log.info(dto);
    }
}
