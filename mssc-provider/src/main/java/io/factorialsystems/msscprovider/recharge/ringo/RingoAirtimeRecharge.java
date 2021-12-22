package io.factorialsystems.msscprovider.recharge.ringo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.RechargeRequest;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.RingoRechargeFactory;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoAirtimeRequest;
import io.factorialsystems.msscprovider.recharge.ringo.response.RingoAirtimeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RingoAirtimeRecharge implements Recharge, ParameterCheck {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final RingoProperties ringoProperties;

    @Override
    public RechargeStatus recharge(RechargeRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("email", ringoProperties.getMail());
        headers.add("password", ringoProperties.getPassword());

        int cost = (int)request.getServiceCost().doubleValue();

        RingoAirtimeRequest airtimeRequest = RingoAirtimeRequest.builder()
                .amount(String.valueOf(cost))
                .request_id(ringoProperties.getAirtimeRequestId())
                .msisdn(request.getRecipient())
                .serviceCode(ringoProperties.getAirtimeServiceCode())
                .product_id(RingoRechargeFactory.codeMapper.get(request.getServiceCode()))
                .build();

        try {
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(airtimeRequest), headers);
            RingoAirtimeResponse response =
                    restTemplate.postForObject(ringoProperties.getAirtimeUrl(), entity, RingoAirtimeResponse.class);

            if (response != null && response.getMessage() != null && response.getMessage().equalsIgnoreCase("Successful")) {
                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message("Ringo Recharge Successful")
                        .build();
            }

            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message("Ringo Recharge Failure")
                    .build();


        } catch (JsonProcessingException e) {
           throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Boolean check(RechargeRequest request) {
        return request != null && request.getRecipient() != null;
    }
}
