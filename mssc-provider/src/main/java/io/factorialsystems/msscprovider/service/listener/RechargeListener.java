package io.factorialsystems.msscprovider.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dto.recharge.AsyncRechargeDto;
import io.factorialsystems.msscprovider.dto.recharge.AsyncRefundResponseDto;
import io.factorialsystems.msscprovider.service.bulkrecharge.NewBulkRechargeService;
import io.factorialsystems.msscprovider.service.bulkrecharge.helper.BulkRefundRecharge;
import io.factorialsystems.msscprovider.service.singlerecharge.SingleRechargeService;
import io.factorialsystems.msscprovider.service.singlerecharge.helper.SingleRefundRecharge;
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

    @JmsListener(destination = JMSConfig.RETRY_RECHARGE_QUEUE)
    public void listenForRetryBulk(String jsonData) throws IOException{

        if (jsonData != null) {
            String id = objectMapper.readValue(jsonData, String.class);

            NewBulkRechargeService rechargeService = applicationContext.getBean(NewBulkRechargeService.class);
            rechargeService.retryFailedRecharges(id);
        }
    }

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

    @JmsListener(destination = JMSConfig.WALLET_REFUND_RESPONSE_QUEUE_PROVIDER)
    public void listenForWalletResponse(String jsonData) throws IOException {

        if (jsonData != null) {
            AsyncRefundResponseDto dto = objectMapper.readValue(jsonData, AsyncRefundResponseDto.class);

            final String singleRechargeId = dto.getRechargeId();
            final String bulkRechargeId = dto.getBulkRechargeId();

            if (singleRechargeId != null && bulkRechargeId != null) {
                final String errorMessage =
                        String.format("Wallet Refund Response has Single recharge Id %s and Bulk Recharge Id %s, only 1 can be set", singleRechargeId, bulkRechargeId);
                log.error(errorMessage);
                return;
            }

            if (dto.getBulkRechargeId() != null) {
                BulkRefundRecharge bulkRefundRecharge = applicationContext.getBean(BulkRefundRecharge.class);
                bulkRefundRecharge.refundRechargeResponse(dto);
            } else if (dto.getRechargeId() != null) {
                SingleRefundRecharge singleRefundRecharge = applicationContext.getBean(SingleRefundRecharge.class);
                singleRefundRecharge.refundRechargeResponse(dto);
            } else {
                log.error ("Wallet Refund Response Recharge Ids not set");
            }
        }
    }
}
