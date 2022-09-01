package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.CacheProxy;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.DataPlanDto;
import io.factorialsystems.msscprovider.dto.recharge.SpectranetRingoDataPlan;
import io.factorialsystems.msscprovider.mapper.recharge.DataPlanMapstructMapper;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.ringo.dto.FetchSpectranetDataDto;
import io.factorialsystems.msscprovider.recharge.ringo.request.SpectranetRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.SpectranetPIN;
import io.factorialsystems.msscprovider.recharge.ringo.response.SpectranetResponse;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RingoSpectranetRecharge implements Recharge, DataEnquiry, ParameterCheck {
    private final CacheProxy cacheProxy;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final RingoProperties properties;
    private final DataPlanMapstructMapper dataPlanMapstructMapper;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {
        SpectranetRequest spectranetRequest = SpectranetRequest.builder()
                .amount(request.getServiceCost())
                .request_id(request.getId())
                .pinNo(String.valueOf(1))
                .serviceCode(properties.getOtherDataServiceCode())
                .type(properties.getSpectranetType())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(K.HEADER_EMAIL, properties.getMail());
        headers.add(K.HEADER_PASSWORD, properties.getPassword());

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(spectranetRequest), headers);
            SpectranetResponse response =
                    restTemplate.postForObject(properties.getAirtimeUrl(), entity, SpectranetResponse.class);

            String errorMessage = null;

            if (response != null && response.getMessage() != null && response.getPin() != null && response.getPin().size() > 0) {
                if (response.getMessage().equalsIgnoreCase("Successful")) {
                    log.info(String.format("Spectranet data Pin purchase (%.2f) Successful", request.getServiceCost()));

                    SpectranetPIN pin = response.getPin().get(0);
                    final String results = String.format("Pin: %s, Serial: %s", pin.getPin(), pin.getSerial());

                    return RechargeStatus.builder()
                            .status(HttpStatus.OK)
                            .message(String.format("Spectranet Purchase Successful, %s", results))
                            .results(results)
                            .build();
                } else {
                    errorMessage = String.format("Error Acquiring Spectranet PIN message (%s)", response.getMessage());
                }
            } else {
                errorMessage = "Unknown Error Acquiring Spectranet Pin";
            }

            log.error(errorMessage);

            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessage)
                    .build();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Cacheable("spectranetdataplans")
    public List<DataPlanDto> getDataPlans(String planCode) {
        log.info("Retrieving Data Plan for Spectranet code {}", planCode);

        FetchSpectranetDataDto fetchSpectranetDataDto = FetchSpectranetDataDto.builder()
                .serviceCode("V-Internet")
                .type("SPECTRANET")
                .build();

        HttpHeaders headers = getHeaders();

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(fetchSpectranetDataDto), headers);
            SpectranetRingoDataPlan response =
                    restTemplate.postForObject(properties.getAirtimeUrl(), entity, SpectranetRingoDataPlan.class);

            if (response == null || response.getProduct() == null) {
                log.error("Error retrieving Spectranet Data Plans from provider, response of response product is NULL");
                return Collections.emptyList();
            }

            return dataPlanMapstructMapper.listSpectranetPlanToDto(response.getProduct());
        } catch (JsonProcessingException e) {
            log.error("Error Processing Spectranet Data Plans Message {}", e.getMessage());
            return Collections.emptyList();
        }

//        String network = RingoRechargeFactory.codeMapper.get(planCode);
//        return dataPlanMapstructMapper.listRingoPlanToDto(ringoDataPlanMapper.findByNetworkId(network));
    }

    @Override
    public DataPlanDto getPlan(String id, String planCode) {
        log.info("Retrieving single spectranet data plan for id {}, code {}", id, planCode);

        return cacheProxy.getRingoSpectranetDataPlans(planCode).stream()
                .filter(p -> p.getProduct_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Unable to load Spwctranet Data Plan %s", id)));


        //return dataPlanMapstructMapper.ringoPlanToDto(ringoDataPlanMapper.findById(id));
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(K.HEADER_EMAIL, properties.getMail());
        headers.add(K.HEADER_PASSWORD, properties.getPassword());

        return headers;
    }
}
