package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.CacheProxy;
import io.factorialsystems.msscprovider.config.CachingConfig;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.DataPlanDto;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.RingoRechargeFactory;
import io.factorialsystems.msscprovider.recharge.ringo.dto.FetchDataDto;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoDataRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoDataResponse;
import io.factorialsystems.msscprovider.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RingoMobileDataRecharge implements Recharge, DataEnquiry, ParameterCheck {
    private final CacheProxy cacheProxy;
    private final CacheManager cacheManager;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RingoProperties ringoProperties;
    private final RingoSpectranetRecharge spectranetRecharge;

    private static HttpHeaders headers = null;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {

        if (request.getServiceCode().equals("SPECTRANET-DATA")) {
            return spectranetRecharge.recharge(request);
        } else if (request.getServiceCode().equals("SMILE-DATA")) {
            return null;
        }

        HttpHeaders headers = getHeaders();
        RingoDataRequest dataRequest = RingoDataRequest.builder()
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

        } catch (Exception e) {
            log.error("Ringo Data Recharge Exception {}", e.getMessage());
            return RechargeStatus.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    @SneakyThrows
    @Cacheable(CachingConfig.RINGO_MOBILE_DATA_PLAN_CACHE)
    public List<DataPlanDto> getDataPlans(String requestCode) {
        log.info("Retrieving Data Plan for Ringo code {}", requestCode);

        String network = RingoRechargeFactory.codeMapper.get(requestCode);

        if (network == null) {
            throw new RuntimeException(String.format("Illegal request code %s", requestCode));
        }

        FetchDataDto fetchDataDto = FetchDataDto.builder()
                .serviceCode("DTA")
                .network(network)
                .build();

        HttpHeaders headers = getHeaders();

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(fetchDataDto), headers);
        DataPlanDto[] response =
                restTemplate.postForObject(ringoProperties.getAirtimeUrl(), entity, DataPlanDto[].class);

        if (response == null || response.length == 0) {
            log.error("Error retrieving data plans {} from Ringo", requestCode);
            return List.of(new DataPlanDto[0]);
        }

        return List.of(response);
    }

    @Override
    public DataPlanDto getPlan(String id, String requestCode) {
        log.info("Retrieving single ringo data plan for id {}, code {}", id, requestCode);

        return cacheProxy.getRingoMobileDataPlans(requestCode)
                .stream()
                .filter(p -> p.getProduct_id().equals(id))
                .findFirst()
                .orElseGet(() -> reloadDataPlan(id, requestCode));
    }

    // For some reason we cannot find the specific data Plan in the list
    // of data plans for the service provider lets retry
    private DataPlanDto reloadDataPlan(String id, String requestCode) {
        List<DataPlanDto> dtoList = cacheProxy.getRingoMobileDataPlans(requestCode);

        final String errorMessage =
                String.format(
                        "Unable to retrieve data plan via cache proxy, performing non-performant reload id: %s, requestCode: %s, dto size %d",
                        id,
                        requestCode,
                        dtoList.size()
                );

        log.error(errorMessage);
        log.error(Arrays.toString(dtoList.toArray()));

        // Call the function to reload the data plans directly from the upstream provider,
        // Bypass the cache, if successful evict and insert recently reloaded values
        List<DataPlanDto> newPlanList = getDataPlans(requestCode);

        if (newPlanList == null || newPlanList.isEmpty()) {
            log.error("Retrieved Plan List {} is either NuLL or Empty", requestCode);
            throw new RuntimeException(errorMessage);
        }

        Optional<DataPlanDto> newPlanDto = newPlanList.stream()
                .filter(p -> p.getProduct_id().equals(id))
                .findFirst();

        if (newPlanDto.isEmpty()) {
            log.error("Again unable to find Plan {} with list {}", id, requestCode);
            log.error(Arrays.toString(newPlanList.toArray()));
            throw new RuntimeException(errorMessage);
        }

        Cache cache = cacheManager.getCache(CachingConfig.RINGO_MOBILE_DATA_PLAN_CACHE);

        if (cache == null) {
            log.error("Cannot retrieve Cache {} for update", CachingConfig.RINGO_MOBILE_DATA_PLAN_CACHE);
        } else {
            cache.evictIfPresent(requestCode);
            cache.put(requestCode, newPlanList);
        }

        return newPlanDto.get();
    }

    private HttpHeaders getHeaders() {
        if (headers == null) {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(Constants.HEADER_EMAIL, ringoProperties.getMail());
            headers.add(Constants.HEADER_PASSWORD, ringoProperties.getPassword());
        }

        return headers;
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null && request.getProductId() != null && request.getRecipient() != null;
    }
}
