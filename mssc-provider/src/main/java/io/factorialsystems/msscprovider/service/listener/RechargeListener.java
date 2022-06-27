package io.factorialsystems.msscprovider.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dto.AsyncRechargeDto;
import io.factorialsystems.msscprovider.service.NewBulkRechargeService;
import io.factorialsystems.msscprovider.service.SingleRechargeService;
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
public class RechargeListener {

    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    @Value("${sleep.value}")
    private Integer sleepValue;

    @JmsListener(destination = JMSConfig.NEW_BULK_RECHARGE_QUEUE)
    public void listenForNewBulkRechargeRequest(String jsonData) throws IOException {

        if (jsonData != null) {
            AsyncRechargeDto dto = objectMapper.readValue(jsonData, AsyncRechargeDto.class);
            NewBulkRechargeService rechargeService = applicationContext.getBean(NewBulkRechargeService.class);
            try {
                Thread.sleep(sleepValue);
                rechargeService.runBulkRecharge(dto);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @JmsListener(destination = JMSConfig.SINGLE_RECHARGE_QUEUE)
    public void listenForSingleRechargeRequest(String jsonData) throws IOException {

        if (jsonData != null) {
            AsyncRechargeDto dto = objectMapper.readValue(jsonData, AsyncRechargeDto.class);
            SingleRechargeService rechargeService = applicationContext.getBean(SingleRechargeService.class);

            try {
                Thread.sleep(sleepValue);
                rechargeService.finishRecharge(dto);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
