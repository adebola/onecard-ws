package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.ExtraDataPlanDto;
import io.factorialsystems.msscprovider.dto.recharge.ExtraPlanRequestDto;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoPayCableRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoValidateCableRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoPayCableResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoValidateCableResponse;
import io.factorialsystems.msscprovider.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class DstvHelper {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RingoProperties ringoProperties;

    private static HttpHeaders httpHeaders = null;

    private HttpHeaders getHeader() {

        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add(Constants.HEADER_EMAIL, ringoProperties.getMail());
            httpHeaders.add(Constants.HEADER_PASSWORD, ringoProperties.getPassword());
        }

        return httpHeaders;
    }

    public ExtraDataPlanDto validateCable(ExtraPlanRequestDto dto) {

        String code = null;

        switch (dto.getServiceCode()) {
            case "DSTV":
                code = ringoProperties.getDstv();
                break;

            case "GOTV":
                code = ringoProperties.getGotv();
                break;

            case "STARTIMES":
                code = ringoProperties.getStartimes();
                break;

            default:
                throw new RuntimeException(String.format("Unknown Service Code in validateCable for (%s) Failed", code));
        }

        RingoValidateCableRequest ringoValidateCableRequest = RingoValidateCableRequest.builder()
                .type(code)
                .smartCardNo(dto.getRecipient())
                .serviceCode(ringoProperties.getCableVerification())
                .build();

        log.info("Validate Cable Request " + ringoValidateCableRequest);

        RingoValidateCableResponse response = null;

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(ringoValidateCableRequest), getHeader());

            response =
                    restTemplate.postForObject(ringoProperties.getAirtimeUrl(), entity, RingoValidateCableResponse.class);

            if (response != null && Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())) {

                log.info("Validate Cable Success");

                return ExtraDataPlanDto.builder()
                        .customerName(response.getCustomerName())
                        .recipient(response.getSmartCardNo())
                        .message(response.getMessage())
                        .status(200)
                        .object(response.getProduct())
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error performing Validation Reason: " + e.getMessage());
        }

        String message;

        if (response != null && response.getMessage() != null) {
            message = String.format("Ringo Validate for %s Cable Failed Reason : %s", ringoValidateCableRequest.getServiceCode(), response.getMessage());
        } else {
            message = String.format("Ringo Validate for %s Cable Failed Reason", ringoValidateCableRequest.getServiceCode());
        }

        return ExtraDataPlanDto.builder()
                .status(400)
                .message(message)
                .build();
    }

    public RechargeStatus recharge(SingleRechargeRequest singleRechargeRequest) {

        RingoPayCableRequest request = RingoPayCableRequest
                .builder()
                .serviceCode(ringoProperties.getCablePayment())
                .request_id(singleRechargeRequest.getId())
                .code(singleRechargeRequest.getProductId())
                .type(singleRechargeRequest.getServiceCode())
                .period("1")
                .smartCardNo(singleRechargeRequest.getRecipient())
                .name(singleRechargeRequest.getName())
                .hasAddon("False")
                .build();

        log.info("Ringo Pay Cable Request: " + request);

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(request), getHeader());

            RingoPayCableResponse response =
                    restTemplate.postForObject(ringoProperties.getAirtimeUrl(), entity, RingoPayCableResponse.class);
            log.info("Cable Recharge Request Response: " + response);

            if (response != null && Objects.equals(response.getStatus(), RingoResponseStatus.SUCCESS.getValue())) {
                log.info("Cable recharge Success");
                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message("Ringo Pay Cable Successful")
                        .build();
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        log.info("Cable Recharge Failure");

        return RechargeStatus.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Ringo Pay Cable Failed")
                .build();
    }
}