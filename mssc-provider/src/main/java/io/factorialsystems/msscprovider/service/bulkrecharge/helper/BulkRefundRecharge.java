package io.factorialsystems.msscprovider.service.bulkrecharge.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.NewBulkRechargeMapper;
import io.factorialsystems.msscprovider.domain.query.IndividualRequestQuery;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.AsyncRefundRequestDto;
import io.factorialsystems.msscprovider.dto.AsyncRefundResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkRefundRecharge {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final NewBulkRechargeMapper bulkRechargeMapper;

    // Refunds a Single Recharge within a Bulk Recharge
    @SneakyThrows
    public void refundRecharge(Integer id, String bulkRequestId) {
        NewBulkRechargeRequest request = bulkRechargeMapper.findBulkRechargeById(bulkRequestId);

        if (request != null && request.getUserId() != null) {
            IndividualRequestQuery query = IndividualRequestQuery.builder()
                    .id(id)
                    .userId(request.getUserId())
                    .build();

            IndividualRequest individualRequest = bulkRechargeMapper.findIndividualRequestById(query);

            if (individualRequest != null && individualRequest.getFailed() &&
                    individualRequest.getRetryId() == null && individualRequest.getRefundId() == null) {
                AsyncRefundRequestDto refundRequest = AsyncRefundRequestDto.builder()
                        .bulkRechargeId(request.getId())
                        .individualRechargeId(id)
                        .amount(individualRequest.getServiceCost())
                        .paymentId(request.getPaymentId())
                        .userId(request.getUserId())
                        .build();

                log.info(String.format("MQ Request sent for Refund of Single Recharge %d in Bulk Recharge for User %s, Amount %.2f", id, request.getUserId(), individualRequest.getServiceCost()));
                jmsTemplate.convertAndSend(JMSConfig.PAYMENT_REFUND_QUEUE, objectMapper.writeValueAsString(refundRequest));

            }
        }
    }

    // Refunds all Failed Individual Recharges in a Bulk Recharge, Except for
    // Successfully Refunded and Successfully Retried Recharges - Overloaded
    public void refundRecharges(String id) {
        NewBulkRechargeRequest request = bulkRechargeMapper.findBulkRechargeById(id);

        if (request != null && request.getUserId() != null) {
            refundRecharges(request);
        }
    }

    // Refunds all Failed Individual Recharges in a Bulk Recharge, Except for
    // Successfully Refunded and Successfully Retried Recharges - Overloaded
    @SneakyThrows
    public void refundRecharges(NewBulkRechargeRequest request) {
        Double aDouble = bulkRechargeMapper.findRefundTotalByRequestId(request.getId());
        BigDecimal totalToRefunded = BigDecimal.valueOf(aDouble == null ? 0 : aDouble);

        if(BigDecimal.ZERO.compareTo(totalToRefunded) == 0) {
            log.error(String.format("Nothing to Refund in Recharge Request %s, may have been refunded, retried or resolved", request.getId()));
            return;
        }

        if (request.getUserId() != null) {
            AsyncRefundRequestDto refundRequest = AsyncRefundRequestDto.builder()
                    .bulkRechargeId(request.getId())
                    .amount(totalToRefunded)
                    .paymentId(request.getPaymentId())
                    .userId(request.getUserId())
                    .build();

            log.info(String.format("MQ Request sent for Refund on Bulk Recharge for User %s, Amount %.2f", request.getUserId(), totalToRefunded));
            jmsTemplate.convertAndSend(JMSConfig.PAYMENT_REFUND_QUEUE, objectMapper.writeValueAsString(refundRequest));
        } else {
            log.error(String.format("Asynchronous Bulk Refund Failed for %s User Not Present", request.getId()));
        }
    }

    // Refund Recharge mechanism is asynchronous and runs by sending MQ Messages
    // This function is called when a Refund Recharge Request has succeeded and
    // our internal needs to be brought needs to be updated accordingly
    public void refundRechargeResponse(AsyncRefundResponseDto dto) {
        log.info(String.format("Received Bulk Recharge Refund Response for %s, Status %d", dto.getBulkRechargeId(), dto.getStatus()));

        if (dto.getStatus() == 200) {
            Double aDouble = bulkRechargeMapper.findRefundTotalByRequestId(dto.getBulkRechargeId());
            BigDecimal totalToRefunded = BigDecimal.valueOf(aDouble == null ? 0 : aDouble);

            if (!totalToRefunded.equals(dto.getAmount())) {
                final String errorMessage = String.format("Total to be Refunded %.2f and Total Refunded %.2f do not match", totalToRefunded, dto.getAmount());
                log.error(errorMessage);
            }

            Map<String, String> refundMap = new HashMap<>();

            if (dto.getIndividualRechargeId() == null) {
                refundMap.put("id", dto.getBulkRechargeId());
                refundMap.put("refundId", dto.getId());
                bulkRechargeMapper.saveBulkRefund(refundMap);
            } else {
                refundMap.put("id", String.valueOf(dto.getIndividualRechargeId()));
                refundMap.put("refundId", dto.getId());
                bulkRechargeMapper.saveIndividualRefund(refundMap);
            }
        }
    }
}
