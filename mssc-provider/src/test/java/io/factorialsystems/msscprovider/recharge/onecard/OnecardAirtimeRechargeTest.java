package io.factorialsystems.msscprovider.recharge.onecard;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@CommonsLog
class OnecardAirtimeRechargeTest {

    @Autowired
    private OnecardAirtimeRecharge recharge;

    @Test
    void getBalance() {
//        BigDecimal balance = recharge.getBalance();
//        log.info(balance);
    }

    @Test
    void loginAndLogout() throws Exception {
//        recharge.getBalance();
//        recharge.logout();
    }

    @Test
    void recharge() {
    }
}