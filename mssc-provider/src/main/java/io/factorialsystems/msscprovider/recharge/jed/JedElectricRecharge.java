package io.factorialsystems.msscprovider.recharge.jed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.RechargeRequest;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.JedRechargeFactory;
import io.factorialsystems.msscprovider.recharge.jed.request.PaymentRequest;
import io.factorialsystems.msscprovider.recharge.jed.response.JedPaymentResponse;
import io.factorialsystems.msscprovider.recharge.jed.response.JedVerifyResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class JedElectricRecharge implements Recharge {
    private final JedProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public RechargeStatus recharge(RechargeRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(JedRechargeFactory.TOKEN, properties.getToken());
        headers.add(JedRechargeFactory.PRIVATE_KEY, properties.getPrivateKey());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        if (request == null || request.getRecipient() == null || request.getServiceCost() == null || request.getTelephone() == null) {
            throw new RuntimeException("Request parameters for recharge missing");
        }

        HttpEntity<JedVerifyResponse> response =
                restTemplate.exchange(properties.getUrl() + "/verifyAccount.php/?customer=" + request.getRecipient(), HttpMethod.GET, entity, JedVerifyResponse.class);

        if (!response.hasBody() || response.getBody() == null) {
            throw new RuntimeException(String.format("Unable to Verify Account (%s)", request.getRecipient()));
        }

        JedVerifyResponse verify = response.getBody();

        if (verify.getStatus() == null || !verify.getStatus().equals("100")) {
            throw new RuntimeException(String.format("Customer verification failure (%s)", request.getRecipient()));
        }

        PaymentRequest paymentRequest = new PaymentRequest(verify.getAccessCode(),
                (int) request.getServiceCost().doubleValue(),
                request.getTelephone()
        );

        try {
            HttpEntity<String> postEntity = new HttpEntity<>(objectMapper.writeValueAsString(paymentRequest), headers);
            JedPaymentResponse paymentResponse =
                    restTemplate.postForObject(properties.getUrl() + "/makePayment.php", postEntity, JedPaymentResponse.class);

            assert (paymentResponse != null && paymentResponse.getStatus() != null);

            if (paymentResponse.getStatus().equals("100")) {
                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message(paymentResponse.getPayDetails().getToken())
                        .build();
            }

            throw new RuntimeException(String.format("Recharge failure reason (%s)", paymentResponse.getMessage()));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
