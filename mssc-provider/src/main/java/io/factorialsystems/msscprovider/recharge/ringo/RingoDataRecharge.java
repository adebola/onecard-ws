package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.dao.RingoDataPlanMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.domain.RingoDataPlan;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.dto.FetchDataDto;
import io.factorialsystems.msscprovider.mapper.recharge.DataPlanMapstructMapper;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.RingoRechargeFactory;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoDataRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RingoDataRecharge implements Recharge, DataEnquiry, ParameterCheck {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RingoProperties ringoProperties;
    private final DataPlanMapstructMapper dataPlanMapper;
    private final RingoDataPlanMapper ringoDataPlanMapper;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {

        HttpHeaders headers = getHeaders();
        RingoDataRequest dataRequest =  RingoDataRequest.builder()
                .request_id(request.getId())
                .msisdn(request.getRecipient())
                .product_id(request.getProductId())
                .serviceCode(ringoProperties.getDataServiceCode())
                .build();

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(dataRequest), headers);
            RingoDataResponse response =
                    restTemplate.postForObject(ringoProperties.getAirtimeUrl(), entity, RingoDataResponse.class);


            if (response != null && response.getMessage() != null && response.getMessage().equalsIgnoreCase("Successful")) {
                log.info(String.format("Ringo data Recharge for (%s) Successful Plan (%s)", request.getRecipient(), request.getProductId()));
                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message("Ringo Data Recharge Successful")
                        .build();
            }

            if (response != null && response.getMessage() != null) {
                log.error(String.format("Ringo data Recharge for (%s) failure Plan (%s) Message (%s)",
                        request.getRecipient(), request.getProductId(), response.getMessage()));
            } else {
                log.error(String.format("Ringo data Recharge for (%s) failure Plan (%s)",
                        request.getRecipient(), request.getProductId()));
            }

            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Ringo Data Recharge failure")
                    .build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<DataPlanDto> getDataPlans(String requestCode) {
        String network = RingoRechargeFactory.codeMapper.get(requestCode);

        if (network == null) {
            throw new RuntimeException(String.format("Illegal request code %s", requestCode));
        }

        FetchDataDto fetchDataDto = FetchDataDto.builder()
                .serviceCode("DTA")
                .network(network)
                .build();

        HttpHeaders headers = getHeaders();

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(fetchDataDto), headers);
            DataPlanDto[] response =
                    restTemplate.postForObject(ringoProperties.getAirtimeUrl(), entity, DataPlanDto[].class);

            log.info(String.format("Retrieving Data Plan for Network %s", network));

            return List.of(response != null ? response : new DataPlanDto[0]);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public DataPlanDto getPlan(String id) {
        RingoDataPlan plan = ringoDataPlanMapper.findById(id);
        if (plan == null) {
            throw new RuntimeException(String.format("Unable to load Data Plan from Database %s", id));
        }

        return dataPlanMapper.ringoPlanToDto(plan);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("email", ringoProperties.getMail());
        headers.add("password", ringoProperties.getPassword());

        return headers;
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null && request.getProductId() != null && request.getRecipient() != null;
    }
}
