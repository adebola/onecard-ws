package io.factorialsystems.msscprovider.recharge.onecard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.recharge.Balance;
import io.factorialsystems.msscprovider.recharge.ParameterCheck;
import io.factorialsystems.msscprovider.recharge.Recharge;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.factory.OnecardRechargeFactory;
import io.factorialsystems.msscprovider.recharge.onecard.request.RequestOnecardAirtimeDto;
import io.factorialsystems.msscprovider.recharge.onecard.response.AirtimeRechargeResponse;
import io.factorialsystems.msscprovider.recharge.onecard.response.BalanceResponse;
import io.factorialsystems.msscprovider.recharge.onecard.response.LoginResponseType;
import io.factorialsystems.msscprovider.recharge.onecard.response.OnecardMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnecardAirtimeRecharge implements Recharge, ParameterCheck, Balance {
    private final ObjectMapper objectMapper;
    private final OnecardConnect onecardConnect;

    @Value("${onecard.api.baseurl}")
    private String baseUrl;

    @Override
    public BigDecimal getBalance() {

        String response = null;

        try {
            LoginResponseType loginResponseType = onecardConnect.doLogin();

            if (loginResponseType == LoginResponseType.LOGIN_SUCCESS || loginResponseType == LoginResponseType.LOGIN_ALREADY_LOGGED_IN) {
                RestTemplate restTemplate = new RestTemplate();
                MultiValueMap<String, String> headers = onecardConnect.getHeaders();

                Map<Object, Object> payload = new HashMap<>();
                HttpEntity<?> request = new HttpEntity<>(payload, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/balance", request, String.class);

                response = onecardConnect.decrypt(responseEntity.getBody());

                log.info(response);
                BalanceResponse balanceResponse = objectMapper.readValue(response, BalanceResponse.class);

                return new BigDecimal((balanceResponse.getResponseData().getBalance()));
            }

            log.error("Login Failed unable to retrieve Balance");
        } catch (JsonProcessingException jex) {
            if (response != null) {
                try {
                    int i = response.indexOf('\"');
                    String s = "{\"" + response.substring(i + 1);

                    OnecardMessageDto messageDto = objectMapper.readValue(s, OnecardMessageDto.class);
                    final String errorMessage = String.format("Error in Balance %s:%s", messageDto.getCode(), messageDto.getMessage());
                    throw new RuntimeException(errorMessage);
                } catch (JsonProcessingException jsonParseException) {
                    final String errorMessage = String.format("Error Processing Error Message in Balance %s", jsonParseException.getMessage());
                    log.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            }

            throw new RuntimeException(String.format("JsonParseException response is NULL : %s", jex.getMessage()));
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }

        return BigDecimal.ZERO;
    }

    @Override
    @SneakyThrows
    public RechargeStatus recharge(SingleRechargeRequest request) {
        LoginResponseType loginResponseType = onecardConnect.doLogin();

        if (loginResponseType == LoginResponseType.LOGIN_SUCCESS || loginResponseType == LoginResponseType.LOGIN_ALREADY_LOGGED_IN) {

            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> headers = onecardConnect.getHeaders();

            String network = OnecardRechargeFactory.codeMapper.get(request.getServiceCode());

            RequestOnecardAirtimeDto dto = new RequestOnecardAirtimeDto(Integer.valueOf(network), request.getRecipient(), request.getServiceCost().intValue(), null, null);
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
            String encryptedRequest = onecardConnect.decrypt(objectMapper.writeValueAsString(dto));
//            String encryptedRequest = Base64.getEncoder().encodeToString(apiCrypt.encryptByte(objectMapper.writeValueAsString(dto)));
            requestBody.add("request", encryptedRequest);

            HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(baseUrl + "/doPayment", HttpMethod.POST, formEntity, String.class);
            String decryptedResponse = onecardConnect.decrypt(response.getBody());

//            String decryptedResponse = new String(apiCrypt.decryptByte(response.getBody())).trim();
            log.info(decryptedResponse);

            AirtimeRechargeResponse airtimeRechargeResponse = objectMapper.readValue(decryptedResponse, AirtimeRechargeResponse.class);

            final String message = airtimeRechargeResponse.getData().get(0).getStatus();

            return RechargeStatus.builder()
                    .message(message)
                    .status(airtimeRechargeResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                    .results(message)
                    .build();
        }

        return RechargeStatus.builder()
                .message("Onecard API Airtime Recharge Login Failure")
                .results("Onecard API Airtime Recharge Failed")
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }
}
