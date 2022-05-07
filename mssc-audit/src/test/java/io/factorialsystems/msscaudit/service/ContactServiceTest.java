package io.factorialsystems.msscaudit.service;

import io.factorialsystems.msscaudit.dto.ContactMessageDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@CommonsLog
@SpringBootTest
class ContactServiceTest {

    @Autowired
    ContactService service;


    @Test
    void saveContactMessage() {
        ContactMessageDto dto = new ContactMessageDto();
        dto.setMessage("New Message");
        dto.setEmail("adeomoboya@gmail.com");
        dto.setPhone("08055572307");
        dto.setName("Adebola Omoboya");

        service.saveContactMessage(dto);
    }

    @Test
    void findById() {
        var x = service.findById("6275acac3d55db238a5236db");
        assertNotNull(x);
        log.info(x);
    }

    @Test
    void findAll() {
        var x = service.findAll(0, 20);
        assertNotNull(x);
        assert(x.getList().size() > 0);
        log.info(x);
    }

    @Test
    void search() {
        var x = service.search(0, 20, "Ad");
        assertNotNull(x);
        assert(x.getList().size() > 0);
        log.info(x.getList().size());
        log.info(x);
    }
}