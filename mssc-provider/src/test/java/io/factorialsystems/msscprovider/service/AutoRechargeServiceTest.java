package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dto.recharge.AutoIndividualRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.AutoRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.AutoRechargeResponseDto;
import io.factorialsystems.msscprovider.utils.K;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@CommonsLog
@SpringBootTest
class AutoRechargeServiceTest {

    @Autowired
    AutoRechargeService autoRechargeService;

    @Test
    void uploadRecharge() {
    }

    @Test
    void saveWeeklyService() {

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            AutoRechargeRequestDto dto = new AutoRechargeRequestDto();

            List<Integer> days = new ArrayList<>();

//        days.add(4);
//        days.add(2);
            days.add(4);

            List<AutoIndividualRequestDto> requests = new ArrayList<>();

            AutoIndividualRequestDto individualRequest = new AutoIndividualRequestDto();
            individualRequest.setServiceCost(new BigDecimal(100));
            individualRequest.setRecipient("08188111333");
            individualRequest.setServiceCode("9-AIRTIME");
            requests.add(individualRequest);

            AutoIndividualRequestDto individualRequest2 = new AutoIndividualRequestDto();
            individualRequest2.setServiceCost(new BigDecimal(155));
            individualRequest2.setRecipient("08055572307");
            individualRequest2.setServiceCode("GLO-AIRTIME");
            requests.add(individualRequest2);

//        dto.setStartDate(new Date());
//
//        Calendar c = Calendar.getInstance();
//        c.setTime(new Date());
//        c.add(Calendar.DATE, 10);
//        dto.setEndDate(c.getTime());

            dto.setPaymentMode("wallet");
            dto.setDaysOfWeek(days);
            dto.setRecipients(requests);

            AutoRechargeResponseDto responseDto = autoRechargeService.saveService(dto);

            log.info(responseDto);

            assertNotNull(responseDto);


        }
    }

    @Test
    void saveServiceInvalidDays() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            AutoRechargeRequestDto dto = new AutoRechargeRequestDto();

            List<Integer> days = new ArrayList<>();

            days.add(8);

            List<AutoIndividualRequestDto> requests = new ArrayList<>();

            AutoIndividualRequestDto individualRequest = new AutoIndividualRequestDto();
            individualRequest.setServiceCost(new BigDecimal(100));
            individualRequest.setRecipient("08188111333");
            individualRequest.setServiceCode("9-AIRTIME");
            requests.add(individualRequest);

            dto.setStartDate(new Date());
            dto.setPaymentMode("wallet");
            dto.setDaysOfWeek(days);
            dto.setRecipients(requests);

            AutoRechargeResponseDto responseDto = autoRechargeService.saveService(dto);
        });

        log.info(exception.getMessage());
    }

    @Test
    void getSingleService() {
//        final String id = "35090c90-5b3c-41d9-a784-686e135358ca";
//        final String id = "2c8ce5e7-f350-4e7c-a5ff-4dba62ba5e50";
//        AutoRechargeRequestDto dto = autoRechargeService.getSingleService(id);
//        log.info(dto);
    }

    @Test
    void updateService() {
        AutoRechargeRequestDto dto = new AutoRechargeRequestDto();
        List<Integer> days = new ArrayList<>();

        days.add(1);
        days.add(2);
        days.add(4);
//        days.add(7);

        List<AutoIndividualRequestDto> requests = new ArrayList<>();

        AutoIndividualRequestDto individualRequest = new AutoIndividualRequestDto();
        individualRequest.setServiceCost(new BigDecimal(100));
        individualRequest.setRecipient("08188111333");
        individualRequest.setServiceCode("9-AIRTIME");
        requests.add(individualRequest);

        AutoIndividualRequestDto individualRequest2 = new AutoIndividualRequestDto();
        individualRequest2.setServiceCost(new BigDecimal(155));
        individualRequest2.setRecipient("08055572307");
        individualRequest2.setServiceCode("GLO-AIRTIME");
        requests.add(individualRequest2);

        dto.setStartDate(new Date());
        dto.setPaymentMode("wallet");
        dto.setTitle("NewTitle");
        dto.setDaysOfWeek(days);
        dto.setRecipients(requests);

        autoRechargeService.updateService("2c8ce5e7-f350-4e7c-a5ff-4dba62ba5e50", dto);
    }


    @Test
    void findUserRecharges() {

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            var x = autoRechargeService.findUserRecharges(1, 20);
            assertNotNull(x);
//            assert(x.size() > 0);
//            log.info(x.size());
            log.info(x);
        }

    }

    @Test
    void deleteService() {
        final String id = "bc17b4c0-98d9-4099-8c40-d9d16699b807";
        autoRechargeService.deleteService(id);
    }

    @Test
    void runAutoRecharge() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            autoRechargeService.runAutoRecharge();
        }
    }

    @Test
    void searchByName() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            var x = autoRechargeService.searchByName("t", 1, 20);
            log.info(x);
            log.info(x.getTotalSize());
        }
    }

    @Test
    void searchByDate() throws ParseException {

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        final String dateString = "30-05-2022 10:15:55 AM";

        Date d = formatter.parse(dateString);

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            var x = autoRechargeService.searchByDate(d, 1, 20);
            log.info(x);
            log.info(x.getTotalSize());
        }
    }
}