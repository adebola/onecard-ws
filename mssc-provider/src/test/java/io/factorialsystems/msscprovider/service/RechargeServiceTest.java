package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.dto.SearchSingleRechargeDto;
import io.factorialsystems.msscprovider.utils.K;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@CommonsLog
class RechargeServiceTest {
    @Autowired
    private SingleRechargeService rechargeService;

    @Autowired
    private SingleRechargeMapper singleRechargeMapper;

    @Test
    void findRequest() {
    }

    @Test
    void refundRechargeRequest() {
//        final String rechargeId = "10867903-ebe3-438e-8ef5-c4f148ee1465";
//        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//
//        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
//            k.when(K::getUserId).thenReturn(id);
//            assertThat(K.getUserId()).isEqualTo(id);
//            log.info(K.getUserId());
//
//            SingleRechargeRequest request = singleRechargeMapper.findById(rechargeId);
//            rechargeService.refundRechargeRequest(request);
//        }
    }

    @Test
    void adminSearch() throws ParseException {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        SearchSingleRechargeDto dto = new SearchSingleRechargeDto();
        dto.setUserId(id);

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.ENGLISH);
        final String dateString = "26-04-2022 10:15:55 AM";
        dto.setPageNumber(1);
        dto.setPageSize(40);

        Date d = formatter.parse(dateString);
        dto.setSearchDate(d);

//        dto.setSearchProduct("GLO");
//        dto.setSearchRecipient("080");
        var x = rechargeService.adminSearch(dto);
        log.info(x);
        log.info(x.getTotalSize());
    }
    @Test
    void search() {
        var x = rechargeService.search("0803", 1, 20);
        log.info(x.getPageSize());
    }

    @Test
    void rechargeMTN() {
//        RechargeRequestDto dto = new RechargeRequestDto();
//        dto.setServiceCode("MTN-AIRTIME");
//        dto.setServiceCost(new BigDecimal(1500));
//        dto.setRecipient("08055572307");
//        log.info(dto);
//        RechargeRequestDto newDto = rechargeService.startRecharge(dto);
//        log.info(newDto);
    }

    @Test
    void rechargeJed() {
//        RechargeRequestDto dto = new RechargeRequestDto();
//        dto.setServiceCode("JED");
//        dto.setServiceCost(new BigDecimal(1223));
//        dto.setRecipient("44000316354");
//        dto.setTelephone("08012345677");
//        log.info(dto);
//
//        RechargeRequestDto newDto = rechargeService.startRecharge(dto);
//        log.info(newDto);
    }

    @Test
    void startRecharge() {
    }

    @Test
    void finishRecharge() {
    }

    @Test
    void getDataPlans() {
        String code = "XYZ";
        Exception exception = assertThrows(RuntimeException.class, () -> {
            rechargeService.getDataPlans(code);
        });

        String expectedMessage = String.format("Unknown data plan (%s) Or Data Plan is not for DATA", code);
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    void getRecharge() {

    }

    @Test
    void getUserRecharges() {
        //final String id = "3ad67afe-77e7-11ec-825f-5c5181925b12";
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            var y = rechargeService.getUserRecharges(K.getUserId(), 1, 20);
            log.info(y);
        }
    }

    @Test
    void getSingleRecharge() {
        final String id = "04c462eb-720c-4c0b-b908-bdbefaf63ec8";
        var recharge = rechargeService.getRecharge(id);
        assertNotNull(recharge);
        log.info(recharge);
    }

    @Test
    void getSingleVoidRecharge() {
        final String id = "04c462eb-720c-4c0b";
        var recharge = rechargeService.getRecharge(id);
        assertNull(recharge);
    }

//    @Test
//    void finishRecharge() {
//        rechargeService.finishRecharge(28);
//    }
}
