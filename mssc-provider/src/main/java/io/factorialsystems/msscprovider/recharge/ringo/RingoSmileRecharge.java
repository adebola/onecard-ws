package io.factorialsystems.msscprovider.recharge.ringo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.dao.RingoDataPlanMapper;
import io.factorialsystems.msscprovider.domain.RingoDataPlan;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.mapper.recharge.DataPlanMapstructMapper;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.RingoRechargeFactory;
import io.factorialsystems.msscprovider.recharge.ringo.request.SmileRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.SmileValidityRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.SmileResponse;
import io.factorialsystems.msscprovider.recharge.ringo.response.SmileValidateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@CommonsLog
@Component
@RequiredArgsConstructor
public class RingoSmileRecharge implements Recharge, DataEnquiry, ParameterCheck {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RingoProperties properties;
    private final RingoDataPlanMapper ringoDataPlanMapper;
    private final DataPlanMapstructMapper dataPlanMapstructMapper;

    @Override
    public List<DataPlanDto> getDataPlans(String planCode) {
        String network = RingoRechargeFactory.codeMapper.get(planCode);
        return dataPlanMapstructMapper.listRingoPlanToDto(ringoDataPlanMapper.findByNetworkId(network));
    }

    @Override
    public DataPlanDto getPlan(String id) {
        return dataPlanMapstructMapper.ringoPlanToDto(ringoDataPlanMapper.findById(id));
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {

        if (request == null || request.getRecipient() == null || request.getProductId() == null) return false;

        SmileValidityRequest validityRequest = SmileValidityRequest.builder()
                .account(request.getRecipient())
                .type(properties.getSmileType())
                .serviceCode(properties.getEnquiryDataCode())
                .build();

        HttpHeaders headers = createHeader();

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(validityRequest), headers);
            SmileValidateResponse response =
                    restTemplate.postForObject(properties.getAirtimeUrl(), entity, SmileValidateResponse.class);

            log.info("SmileRecharge Parameter check");
            log.info(response);

            return (response != null && response.getStatus() == 200);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {

        RingoDataPlan dataPlan = ringoDataPlanMapper.findById(request.getProductId());

        if (dataPlan != null) {
            SmileRequest smileRequest = SmileRequest.builder()
                    .allowance(dataPlan.getAllowance())
                    .code(dataPlan.getId())
                    .name(dataPlan.getAllowance())
                    .price(request.getServiceCost())
                    .account(request.getRecipient())
                    .request_id(request.getId())
                    .validity(dataPlan.getValidity())
                    .serviceCode(properties.getOtherDataServiceCode())
                    .type(properties.getSmileType())
                    .build();

            HttpHeaders headers = createHeader();

            try {
                HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(smileRequest), headers);
                SmileResponse response =
                        restTemplate.postForObject(properties.getAirtimeUrl(), entity, SmileResponse.class);

                String errorMessage = null;
                log.info("Smile Response after call to postForObject to Ringo Backend");
                log.info(response);

                if (response != null && response.getMessage() != null && response.getMessage().equals("Successful")) {
                    log.info(String.format("Successful SMILE Request (%s/%s)", request.getId(), request.getProductId()));
                    return RechargeStatus.builder()
                            .status(HttpStatus.OK)
                            .message("Smile recharge Successful")
                            .build();
                } else {
                    errorMessage = String.format("Error Recharging SMILE Request %s/%s/%s", request.getRecipient(), request.getProductId(), dataPlan.getAllowance());
                    log.error(errorMessage);
                }

                if (response != null && response.getMessage() != null) {
                    log.error(String.format("Smile Recharge failure for %s product %s, Reason %s",request.getRecipient(), request.getProductId(), response.getMessage()));
                } else {
                    log.error(String.format("Smile Recharge failure for %s product %s NULL Response", request.getRecipient(), request.getProductId()));
                }

            } catch(JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return RechargeStatus.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("SMILE request Error, please contact support")
                .build();
    }

    private HttpHeaders createHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("email", properties.getMail());
        headers.add("password", properties.getPassword());

        return headers;
    }
}
