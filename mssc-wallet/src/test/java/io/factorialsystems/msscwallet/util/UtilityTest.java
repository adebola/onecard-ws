package io.factorialsystems.msscwallet.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

@Slf4j
public class UtilityTest {

    @Test
    void bigDecimalTest() {
        BigDecimal zero = BigDecimal.ZERO;
        BigDecimal decimal = new BigDecimal(-10);

        BigDecimal runningTotal = new BigDecimal(1400);
        BigDecimal limit = new BigDecimal(1000);

        int j = runningTotal.compareTo(limit);
        log.info("J is {}", j);

        int i = decimal.compareTo(zero);
        log.info("Value of Comparison is {}", i);

    }


    @Test
    void RandomNumbers() {
        Random random = new Random();

        for (int i=0; i < 100; i++) {
            String code = String.format("%06d", random.nextInt(10000));
            log.info(code);
        }
    }

    @Test
    void dateformat() {
        final String pattern = "EEEEE dd MMMMM yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        log.info(simpleDateFormat.format(new Date()));
    }

    @Test
    void dateFormat2() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        log.info(formatter.format(OffsetDateTime.now()));
    }

    @Test
    void random() {
        Random random = new Random();

        for (int i=0; i<100; i++) {
            int r = random.nextInt(1_000_000);
            log.info(String.format("%06d %d",r, r));
        }
    }
}
