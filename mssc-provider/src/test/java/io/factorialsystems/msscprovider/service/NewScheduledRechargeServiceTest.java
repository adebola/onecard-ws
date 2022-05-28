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
class NewScheduledRechargeServiceTest {

    @Autowired
    private NewScheduledRechargeService service;


    @Test
    void getUserRecharges() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            var y = service.getUserRecharges(1, 20);

            log.info(y);
            log.info(y.getTotalSize());
        }
    }

    @Test
    void getBulkIndividualRequests() {
        final String id = "bc17bff1-3913-483b-9ee5-ffa9f4fe237d";
        var x = service.getBulkIndividualRequests(id, 1, 20);
        log.info(x);
        log.info(x.getTotalSize());
    }
}