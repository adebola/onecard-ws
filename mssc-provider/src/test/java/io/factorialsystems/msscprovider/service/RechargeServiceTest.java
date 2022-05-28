package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.utils.K;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@CommonsLog
class RechargeServiceTest {

    @Autowired
    private SingleRechargeService rechargeService;

    @Test
    void findRequest() {
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

            var y = rechargeService.getUserRecharges(1, 20);
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
