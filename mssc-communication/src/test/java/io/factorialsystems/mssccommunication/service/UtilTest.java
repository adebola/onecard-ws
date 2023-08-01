package io.factorialsystems.mssccommunication.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class UtilTest {

    @Test
    void BCryptTest() {
        final String raw = "secret";
        final String encoded = "$2a$12$N8Gpb0wvcFfakCG54QJs6.vyKAxgdBIX05BfVrnBGsh0kPGXY3.wC";
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        final boolean matches = passwordEncoder.matches(raw, encoded);

       log.info("Matches is {}", matches);
    }
}
