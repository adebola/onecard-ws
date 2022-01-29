package io.factorialsystems.msscprovider.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.service.BulkRechargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkRechargeListener {

    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    @Value("${sleep.value}")
    private Integer sleepValue;

    @JmsListener(destination = JMSConfig.BULK_RECHARGE_QUEUE)
    public void listenForBulkRechargeRequest(String jsonData) throws IOException {

        if (jsonData != null) {
            String requestId = objectMapper.readValue(jsonData, String.class);

            BulkRechargeService rechargeService = applicationContext.getBean(BulkRechargeService.class);
            try {
                Thread.sleep(sleepValue);
                rechargeService.runBulkRecharge(requestId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
