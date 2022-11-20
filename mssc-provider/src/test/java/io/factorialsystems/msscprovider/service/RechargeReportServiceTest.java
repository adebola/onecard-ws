package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.RechargeReportMapper;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@CommonsLog
@SpringBootTest
class RechargeReportServiceTest {

    @Autowired
    private RechargeReportMapper mapper;

    @Test
    void getShortRechargeExpenditure() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.ENGLISH);
        String dateInString = "22-01-2015 10:15:55 AM";
        Date date = formatter.parse(dateInString);

        RechargeProviderRequestDto dto = new RechargeProviderRequestDto();
        dto.setStartDate(date);

        var x = mapper.findRechargeProviderExpenditure(dto);
        assertNotNull(x);
        log.info(x);
    }
}