package io.factorialsystems.msscusers.service;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CommonsLog
class RoleServiceTest {

    @Autowired
    RoleService roleService;

    @Test
    void getRoles() {
//        var x = roleService.getRoleById("zzz");
//        log.info(x);

    }

    @Test
    void getRoleById() {
    }

    @Test
    void getRoleByName() {
    }
}
