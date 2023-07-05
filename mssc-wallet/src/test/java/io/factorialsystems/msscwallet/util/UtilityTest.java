package io.factorialsystems.msscwallet.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

@Slf4j
public class UtilityTest {

    @Test
    void bigDecimalTest() {
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal decimal = new BigDecimal(0);

        int i = decimal.compareTo(zero);
        log.info("Value of Comparison is {}", i);

    }
}
