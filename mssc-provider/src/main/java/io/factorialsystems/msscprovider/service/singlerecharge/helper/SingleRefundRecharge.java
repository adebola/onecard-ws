package io.factorialsystems.msscprovider.service.singlerecharge.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.dto.recharge.AsyncRefundRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.AsyncRefundResponseDto;
import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.exception.ResourceNotFoundException;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SingleRefundRecharge {
    private final ObjectMapper objectMapper;
    private final JmsTemplate jmsTemplate;
    private final SingleRechargeMapper singleRechargeMapper;

    @Value("${api.local.host.baseurl}")
    private String baseUrl;

    // Asynchronous Response to Asynchronous Refund Request
    public void refundRechargeResponse(AsyncRefundResponseDto dto) {

        log.info(String.format("Received Recharge Refund Response for %s, Status %d", dto.getRechargeId(), dto.getStatus()));

        if (dto.getStatus() == 200) {
            Map<String, String> refundMap = new HashMap<>();
            refundMap.put("id", dto.getRechargeId());
            refundMap.put("refundId", dto.getId());
            Boolean result = singleRechargeMapper.saveRefund(refundMap);

            if (result) {
                log.info("Recharge {} Update Successfully with RefundId {}", dto.getRechargeId(), dto.getId());
            } else {
                log.error("Recharge {} Update Failure with RefundId {}", dto.getRechargeId(), dto.getId());
            }
        }
    }

    // Refund Single Recharge Synchronously
    @SneakyThrows
    public MessageDto refundRecharge(String id) {
        SingleRechargeRequest request = Optional.ofNullable(singleRechargeMapper.findById(id))
                .orElseThrow(() -> new ResourceNotFoundException("SingleRechargeRequest", "id", id));

        if (request.getUserId() == null) {
            throw new RuntimeException("Unable to Refund No User Present");
        }

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(Objects.requireNonNull(K.getAccessToken()));

        RefundRequestDto refundRequestDto = RefundRequestDto.builder()
                .userId(request.getUserId())
                .amount(request.getServiceCost())
                .build();

        HttpEntity<String> httpRequest = new HttpEntity<>(objectMapper.writeValueAsString(refundRequestDto), headers);

        ResponseEntity<RefundResponseDto> response
                = restTemplate.exchange (baseUrl + "/api/v1/payment/refund/" + request.getPaymentId(), HttpMethod.PUT, httpRequest, RefundResponseDto.class);

        RefundResponseDto dto = response.getBody();

        if (dto != null && dto.getStatus() == 200) {
            Map<String, String> refundMap = new HashMap<>();
            refundMap.put("id", request.getId());
            refundMap.put("refundId", dto.getId());
            singleRechargeMapper.saveRefund(refundMap);

            return new MessageDto(String.format("Single Recharge Refunded Successfully for %s", id));
        }

        return new MessageDto(String.format("Single Refund Failure for %s", id));
    }

    // Refund Single Recharge Asynchronously
    @SneakyThrows
    public void asyncRefundRecharge(SingleRechargeRequest request) {

        // You can only Refund if the User is not anonymous, otherwise we don't know who to refund to
        if (request.getUserId() != null) {
            AsyncRefundRequestDto refundRequest = AsyncRefundRequestDto.builder()
                    .singleRechargeId(request.getId())
                    .amount(request.getServiceCost())
                    .paymentId(request.getPaymentId())
                    .userId(request.getUserId())
                    .build();

            log.info(String.format("MQ Request sent for Refund on Single Recharge for User %s, Amount %.2f", request.getUserId(), request.getServiceCost()));
            jmsTemplate.convertAndSend(JMSConfig.PAYMENT_REFUND_QUEUE, objectMapper.writeValueAsString(refundRequest));
        } else {
            log.error(String.format("Asynchronous Single Recharge Refund Failed for %s, User Not Present", request.getId()));
        }
    }
}
