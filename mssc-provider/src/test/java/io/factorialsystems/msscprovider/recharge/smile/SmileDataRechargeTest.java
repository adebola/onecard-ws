package io.factorialsystems.msscprovider.recharge.smile;

import io.factorialsystems.msscprovider.dto.DataPlanDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@CommonsLog
class SmileDataRechargeTest {

    @Autowired
    private SmileDataRecharge recharge;

    @Test
    void getBalance() {
        BigDecimal balance = recharge.getBalance();
        log.info(balance);
    }

    @Test
    void getDataPlans() {
        List<DataPlanDto> dto = recharge.getDataPlans("");
        log.info(dto);
    }

    @Test
    void check() {
    }

    @Test
    void recharge() {
    }
}