package io.factorialsystems.msscprovider.recharge.smile;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CommonsLog
class SmileDataRechargeTest {

    @Autowired
    private SmileDataRecharge recharge;

    @Test
    void getBalance() {
//        BigDecimal balance = recharge.getBalance();
//        log.info(balance);
    }

    @Test
    void getDataPlans() {
//        List<DataPlanDto> dto = recharge.getDataPlans("");
//        log.info(dto);
    }

    @Test
    void check() {
    }

    @Test
    void recharge() {
    }
}