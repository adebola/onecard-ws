package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.factory.RingoRechargeFactory;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoAirtimeRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoInfoRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoReQueryRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoAirtimeResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoReQueryResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.info.RingoInfoResponse;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class RingoAirtimeRecharge implements Recharge, ParameterCheck, Balance, ReQuery {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RingoProperties ringoProperties;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(K.HEADER_EMAIL, ringoProperties.getMail());
        headers.add(K.HEADER_PASSWORD, ringoProperties.getPassword());

        int cost = (int)request.getServiceCost().doubleValue();

        RingoAirtimeRequest airtimeRequest = RingoAirtimeRequest.builder()
                .amount(String.valueOf(cost))
                .request_id(request.getId())
                .msisdn(request.getRecipient())
                .serviceCode(ringoProperties.getAirtimeServiceCode())
                .product_id(RingoRechargeFactory.codeMapper.get(request.getServiceCode()))
                .build();

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(airtimeRequest), headers);
            RingoAirtimeResponse response =
                    restTemplate.postForObject(ringoProperties.getAirtimeUrl(), entity, RingoAirtimeResponse.class);

            if (response != null && response.getMessage() != null && response.getMessage().equalsIgnoreCase("Successful")) {
                log.info("Successful Ringo Airtime Recharge for {} cost {}", airtimeRequest.getMsisdn(), cost);
                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message("Ringo Recharge Successful")
                        .build();
            }

            String errorMessage = null;

            if (response != null && response.getMessage() != null) {
                errorMessage = String.format("Ringo Airtime Recharge failure for %s cost %d, Reason: %s", airtimeRequest.getMsisdn(), cost, response.getMessage());
                log.error(errorMessage);
            } else {
                errorMessage = String.format("Ringo Airtime Recharge failure for %s cost %d, NULL Response", airtimeRequest.getMsisdn(), cost);
                log.error(errorMessage);
            }

            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessage)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Ringo Recharge Exception {}", e.getMessage());
            return RechargeStatus.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null && request.getRecipient() != null && request.getServiceCost() != null;
    }

    @Override
    public BigDecimal getBalance() {

        RingoInfoRequest request = RingoInfoRequest.builder()
                .serviceCode("INFO")
                .build();

        HttpHeaders headers = getHeader();

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
            RingoInfoResponse response =
                    restTemplate.postForObject(ringoProperties.getAirtimeUrl(), entity, RingoInfoResponse.class);

            if (response != null && response.getStatus() != null && response.getStatus().equals("200")) {
                return new BigDecimal(response.getWallet().getWallet().getBalance());
            }

            return BigDecimal.ZERO;

        } catch (JsonProcessingException e) {
            log.error("Ringo Recharge Exception {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public ReQueryRequestStatus reQueryRequest(ReQueryRequest request) {
        HttpHeaders headers = getHeader();

        RingoReQueryRequest ringoReQueryRequest = new RingoReQueryRequest();
        ringoReQueryRequest.setRequest_id(request.getId());

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(ringoReQueryRequest), headers);

            RingoReQueryResponse response =
                    restTemplate.postForObject("https://www.api.ringo.ng/api/b2brequery", entity, RingoReQueryResponse.class);

            if (response != null && response.getMessage() != null && !response.getMessage().isEmpty()) {
                switch (response.getMessage()) {
                    case "failed":
                        return ReQueryRequestStatus.FAILED;

                    case "pending":
                        return ReQueryRequestStatus.PENDING;

                    case "success":
                        return ReQueryRequestStatus.SUCCESSFUL;
                }
            }

        } catch (JsonProcessingException e) {
            log.error("Ringo ReQuery Exception {}", e.getMessage());
        }

        return ReQueryRequestStatus.UNKNOWN;
    }

    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(K.HEADER_EMAIL, ringoProperties.getMail());
        headers.add(K.HEADER_PASSWORD, ringoProperties.getPassword());

        return headers;
    }
}
