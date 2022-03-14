package io.factorialsystems.msscprovider.service;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@CommonsLog
class RechargeServiceTest {

    @Autowired
    private SingleRechargeService rechargeService;

    @Test
    void findRequest() {
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

//    @Test
//    void finishRecharge() {
//        rechargeService.finishRecharge(28);
//    }
}
