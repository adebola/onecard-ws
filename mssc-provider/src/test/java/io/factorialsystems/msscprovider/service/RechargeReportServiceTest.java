package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.RechargeReportMapper;
import io.factorialsystems.msscprovider.domain.CombinedRechargeList;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import io.factorialsystems.msscprovider.dto.report.RechargeReportRequestDto;
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

    @Autowired
    private RechargeReportService service;

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

    @Test
    void runRechargeReport_All() {
        RechargeReportRequestDto dto = new RechargeReportRequestDto();
        CombinedRechargeList combinedRechargeRequests = service.runRechargeReport(dto);
        assertNotNull(combinedRechargeRequests);

        log.info(combinedRechargeRequests);
    }

    @Test
    void runRechargeReport_Single() {
        RechargeReportRequestDto dto = new RechargeReportRequestDto();
        dto.setType("single");
        CombinedRechargeList combinedRechargeRequests = service.runRechargeReport(dto);
        assertNotNull(combinedRechargeRequests);

        log.info(combinedRechargeRequests);
    }

    @Test
    void runRechargeReport_Bulk() {
        RechargeReportRequestDto dto = new RechargeReportRequestDto();
        dto.setType("bulk");

        CombinedRechargeList combinedRechargeRequests = service.runRechargeReport(dto);
        assertNotNull(combinedRechargeRequests);

        log.info(combinedRechargeRequests);
    }
}