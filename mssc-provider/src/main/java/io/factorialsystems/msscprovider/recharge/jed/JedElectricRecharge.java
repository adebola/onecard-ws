package io.factorialsystems.msscprovider.recharge.jed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.ExtraDataPlanDto;
import io.factorialsystems.msscprovider.dto.recharge.ExtraPlanRequestDto;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.factory.JedRechargeFactory;
import io.factorialsystems.msscprovider.recharge.jed.request.PaymentRequest;
import io.factorialsystems.msscprovider.recharge.jed.response.JedPaymentResponse;
import io.factorialsystems.msscprovider.recharge.jed.response.JedVerifyResponse;
import io.factorialsystems.msscprovider.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class JedElectricRecharge implements Recharge, ParameterCheck, ExtraDataEnquiry, Balance {
    private final JedProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private static HttpHeaders headers = null;

    @Override
    public RechargeStatus recharge(SingleRechargeRequest request) {

        if (request == null || request.getRecipient() == null || request.getServiceCost() == null || request.getTelephone() == null) {
            log.error("Missing parameters in Jed Recharge Request {}", request);
            throw new RuntimeException("Request parameters for recharge missing");
        }

        ExtraPlanRequestDto dto = ExtraPlanRequestDto.builder()
                .recipient(request.getRecipient())
                .serviceCode(request.getServiceCode())
                .build();

        JedVerifyResponse verifyResponse = verifyPlan(dto);

        PaymentRequest paymentRequest = new PaymentRequest(verifyResponse.getAccessCode(),
                request.getServiceCost().intValue(),
                request.getTelephone()
        );

        HttpHeaders headers = getHeader();

        try {
            HttpEntity<String> postEntity = new HttpEntity<>(objectMapper.writeValueAsString(paymentRequest), headers);
            JedPaymentResponse paymentResponse =
                    restTemplate.postForObject(properties.getUrl() + "/makePayment.php", postEntity, JedPaymentResponse.class);

            if (paymentResponse != null && paymentResponse.getStatus() != null && paymentResponse.getStatus().equals("100")) {
               JedPaymentResponse.PayDetails payDetails = paymentResponse.getPayDetails();
               final String results = String.format("Token: %s, Units: %s", payDetails.getToken(), payDetails.getUnits());

                log.info("Successful JED Electric Recharge {}", results);

                return RechargeStatus.builder()
                        .status(HttpStatus.OK)
                        .message(results)
                        .results(results)
                        .build();
            }

            String errorMessage;

            if (paymentResponse != null && paymentResponse.getMessage() != null) {
                errorMessage = String.format("Recharge failure reason (%s)", paymentResponse.getMessage());
            } else {
                errorMessage = "Recharge Failure";
            }

            log.error("JED Recharge failure {}", errorMessage);

            return RechargeStatus.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .message(errorMessage)
                    .build();

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Boolean check(SingleRechargeRequest request) {
        return request != null &&
                request.getRecipient() != null &&
                request.getServiceCost() != null &&
                request.getServiceCost().compareTo(Constants.MINIMUM_RECHARGE_VALUE) > 0 &&
                request.getTelephone() != null;
    }

    @Override
    public BigDecimal getBalance() {
        return new BigDecimal(0);
    }

    @Override
    public ExtraDataPlanDto getExtraPlans(ExtraPlanRequestDto dto) {

        JedVerifyResponse verify = verifyPlan(dto);

        return ExtraDataPlanDto.builder()
                .status(200)
                .customerName(verify.getCustomer().getName())
                .recipient(verify.getCustomer().getMeterNumber())
                .build();
    }

    private JedVerifyResponse verifyPlan(ExtraPlanRequestDto dto) {

        HttpHeaders headers = getHeader();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        HttpEntity<JedVerifyResponse> response =
                restTemplate.exchange(properties.getUrl() + "/verifyAccount.php/?customer=" + dto.getRecipient(), HttpMethod.GET, entity, JedVerifyResponse.class);

        if (!response.hasBody() || response.getBody() == null) {
            throw new RuntimeException(String.format("Unable to Verify Account (%s)", dto.getRecipient()));
        }

        JedVerifyResponse verify = response.getBody();

        if (verify.getStatus() == null || !verify.getStatus().equals("100")) {
            log.error("Verification Error status {}", verify);
            throw new RuntimeException(String.format("Customer verification failure (%s)", dto.getRecipient()));
        }

        return verify;
    }

    private HttpHeaders getHeader() {

        if (headers == null) {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add(JedRechargeFactory.TOKEN, properties.getToken());
            headers.add(JedRechargeFactory.PRIVATE_KEY, properties.getPrivateKey());
        }

        return headers;
    }
}
