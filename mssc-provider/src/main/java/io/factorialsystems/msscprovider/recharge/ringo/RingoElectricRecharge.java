package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.ExtraDataPlanDto;
import io.factorialsystems.msscprovider.dto.recharge.ExtraPlanRequestDto;
import io.factorialsystems.msscprovider.recharge.ExtraDataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.RingoRechargeFactory;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoElectricRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoElectricVerifyRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoElectricResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoElectricVerifyResponse;
import io.factorialsystems.msscprovider.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class RingoElectricRecharge implements Recharge, ParameterCheck, ExtraDataEnquiry {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RingoProperties ringoProperties;

    private static final String[] accountTypes = {"PREPAID", "POSTPAID"};
    private static HttpHeaders headers = null;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {

        String errorMessage = "Electric Error Recharging via Ringo";

        ExtraPlanRequestDto dto = ExtraPlanRequestDto.builder()
                .accountType(request.getAccountType())
                .recipient(request.getRecipient())
                .serviceCode(request.getServiceCode())
                .build();

        ExtraDataPlanDto extraDto = verifyPlan(dto);

        if (extraDto != null && extraDto.getStatus() == 200) {
            int cost = (int) request.getServiceCost().doubleValue();

            RingoElectricRequest electricRequest = RingoElectricRequest.builder()
                    .amount(String.valueOf(cost))
                    .phonenumber(request.getTelephone())
                    .request_id(request.getId())
                    .type(request.getAccountType().toUpperCase())
                    .serviceCode(ringoProperties.getElectricServiceCode())
                    .meterNo(request.getRecipient())
                    .disco(RingoRechargeFactory.codeMapper.get(request.getServiceCode()))
                    .build();

            HttpHeaders headers = getHeader();

            try {
                HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(electricRequest), headers);
                RingoElectricResponse response =
                        restTemplate.postForObject(ringoProperties.getAirtimeUrl(), entity, RingoElectricResponse.class);

                if (response != null && response.getMessage() != null && response.getMessage().equalsIgnoreCase("Successful")) {
                    final String results = String.format("Token: %s Units: %s", response.getToken(), response.getUnit());
                    final String message =
                            String.format("%s: units of Electricity, Token: %s, for %s, Transaction Reference %s",
                                    response.getUnit(), response.getToken(), response.getDisco(), response.getTransRef());

                    log.info("Successful Ringo Electric Recharge {}", message);

                    return RechargeStatus.builder()
                            .status(HttpStatus.OK)
                            .results(results)
                            .message(message)
                            .build();
                }

                errorMessage = String.format("Error Recharging %s via Ringo", request.getServiceCode());

            } catch (Exception e) {
                errorMessage = String.format("Ringo Electric Recharge Error %s", e.getMessage());
            }
        }

        log.error(errorMessage);
        return RechargeStatus.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(errorMessage)
                .build();
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {

        if (request != null && request.getAccountType() != null) {
            if (Arrays.stream(accountTypes).filter(a -> a.equalsIgnoreCase(request.getAccountType())).findFirst().isEmpty()) {
                return false;
            }

            return request.getRecipient() != null &&
                    request.getServiceCost() != null &&
                    request.getServiceCost().compareTo(Constants.MINIMUM_RECHARGE_VALUE) > 0 &&
                    request.getTelephone() != null;
        }

        return false;
    }

    @Override
    public ExtraDataPlanDto getExtraPlans(ExtraPlanRequestDto dto) {
        return verifyPlan(dto);
    }

    private ExtraDataPlanDto verifyPlan(ExtraPlanRequestDto dto) {

        if (dto.getAccountType() == null || (Arrays.stream(accountTypes).filter(a -> a.equalsIgnoreCase(dto.getAccountType())).findFirst().isEmpty()) ) {
            throw new RuntimeException(String.format("AccountType is either not supplied or invalid in the request (%s)", dto.getAccountType()));
        }

        String disco =  RingoRechargeFactory.codeMapper.get(dto.getServiceCode());

        if (disco == null) {
            throw new RuntimeException("Invalid ServiceCode : " + dto.getServiceCode());
        }

        RingoElectricVerifyRequest verifyRequest = RingoElectricVerifyRequest.builder()
                .type(dto.getAccountType().toUpperCase())
                .serviceCode(ringoProperties.getElectricVerifyServiceCode())
                .meterNo(dto.getRecipient())
                .disco(disco)
                .build();

        HttpHeaders headers = getHeader();

        try {
            HttpEntity<String> verifyEntity = new HttpEntity<>(objectMapper.writeValueAsString(verifyRequest), headers);
            RingoElectricVerifyResponse verifyResponse =
                    restTemplate.postForObject(ringoProperties.getAirtimeUrl(), verifyEntity, RingoElectricVerifyResponse.class);

            if (verifyResponse == null || !verifyResponse.getStatus().equals("200")) {
                String errMessage = null;

                if (verifyResponse != null && verifyResponse.getMessage() != null) {
                    errMessage = String.format("Error Validating Meter / Account Number: (%s) reason (%s)", dto.getRecipient(), verifyResponse.getMessage());
                } else {
                    errMessage = "Error Validating Meter / Account Number";
                }

                log.error(errMessage);

                return ExtraDataPlanDto.builder()
                        .customerName(errMessage)
                        .recipient(errMessage)
                        .status(300)
                        .build();
            }

            return ExtraDataPlanDto.builder()
                    .customerName(verifyResponse.getCustomerName())
                    .recipient(verifyResponse.getMeterNo())
                    .status(200)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Ringo Electric Recharge Exception on Account Verification {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private HttpHeaders getHeader() {

        if (headers == null) {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(Constants.HEADER_EMAIL, ringoProperties.getMail());
            headers.add(Constants.HEADER_PASSWORD, ringoProperties.getPassword());
        }

        return headers;
    }
}
