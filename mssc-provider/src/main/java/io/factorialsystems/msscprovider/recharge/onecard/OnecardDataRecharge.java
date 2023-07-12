package io.factorialsystems.msscprovider.recharge.onecard;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.CacheProxy;
import io.factorialsystems.msscprovider.config.CachingConfig;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.DataPlanDto;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.OnecardRechargeFactory;
import io.factorialsystems.msscprovider.recharge.onecard.response.OnecardDataPlanResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnecardDataRecharge implements Recharge, ParameterCheck, DataEnquiry {
    private final CacheProxy cacheProxy;
    private final ObjectMapper objectMapper;
    private final OnecardConnect onecardConnect;
    private final OnecardAirtimeRecharge airtimeRecharge;

    @Value("${onecard.api.baseurl}")
    private String baseUrl;

    @Override
    @Cacheable(CachingConfig.ONECARD_PLAN_CACHE)
    public List<DataPlanDto> getDataPlans(String requestCode) {
        log.info("Retrieving Data Plan for Onecard code {}", requestCode);

        final String network = OnecardRechargeFactory.codeMapper.get(requestCode);

        if (network == null) {
            throw new RuntimeException(String.format("Onecard Illegal request code %s", requestCode));
        }

        final RestTemplate restTemplate = new RestTemplate();
        final MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        try {
            requestBody.add("product_id", onecardConnect.encrypt(network));

            final HttpEntity<?> request = new HttpEntity<>(requestBody, onecardConnect.getHeaders());
            final ResponseEntity<String> responseEntity
                    = restTemplate.postForEntity(baseUrl + "/params", request, String.class);

            final String response = onecardConnect.decrypt(responseEntity.getBody());
            JsonNode apiNode = objectMapper.readTree(response).path("RESPONSE_DATA").path("api_plans");
            OnecardDataPlanResponse[] plans = objectMapper.readValue(apiNode.toString(), OnecardDataPlanResponse[].class);

            if (plans.length > 0) {
                return Arrays.stream(plans)
                        .map(p -> p.getPlans()
                                .stream()
                                .map(plan -> DataPlanDto.builder()
                                        .product_id(plan.getId())
                                        .price(plan.getCurrencyAmount().toString())
                                        .allowance(plan.getInstructions())
                                        .build())
                                .collect(Collectors.toList()))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
            }

            log.error("No Data Plans found for {}", requestCode);
            return new ArrayList<>();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataPlanDto getPlan(String id, String planCode) {
        return cacheProxy.getOnecardPlans(planCode)
                .stream()
                .filter(p -> p.getProduct_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Unable to find Plan %s in cached Onecard Plans", id)));
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return ParameterCheck.super.check(request);
    }

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {
        return airtimeRecharge.recharge(request);
    }
}
