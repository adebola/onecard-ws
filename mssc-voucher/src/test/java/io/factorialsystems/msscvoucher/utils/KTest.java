package io.factorialsystems.msscvoucher.utils;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@CommonsLog
class KTest {

    @Test
    public void testGenerateRandom() {

        for (int i = 0; i < 10; i++) {
            log.info(K.generateRandomNumber(12));
        }
    }
}
