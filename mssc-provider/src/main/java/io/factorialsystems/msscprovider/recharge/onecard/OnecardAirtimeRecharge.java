package io.factorialsystems.msscprovider.recharge.onecard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.CacheProxy;
import io.factorialsystems.msscprovider.config.CachingConfig;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.DataPlanDto;
import io.factorialsystems.msscprovider.recharge.*;
import io.factorialsystems.msscprovider.recharge.factory.OnecardRechargeFactory;
import io.factorialsystems.msscprovider.recharge.factory.RingoRechargeFactory;
import io.factorialsystems.msscprovider.recharge.onecard.request.RequestOnecardAirtimeDto;
import io.factorialsystems.msscprovider.recharge.onecard.response.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnecardAirtimeRecharge implements Recharge, ParameterCheck, Balance, DataEnquiry {
    private final MCrypt loginCrypt;
    private final CacheProxy cacheProxy;
    private final ObjectMapper objectMapper;

    @Value("${onecard.api.baseurl}")
    private String baseUrl;

    @Value("${onecard.api.user}")
    private String user;

    @Value("${onecard.api.password}")
    private String password;

    @Value("${onecard.api.salt}")
    private String iVSecret;

    private String userToken;
    private String authToken;
    private MCrypt apiCrypt = null;
    private Date expiry;

    @Override
    public BigDecimal getBalance() {

        String response = null;

        try {
            LoginResponseType loginResponseType = doLogin();

            if (loginResponseType == LoginResponseType.LOGIN_SUCCESS || loginResponseType == LoginResponseType.LOGIN_ALREADY_LOGGED_IN) {
                RestTemplate restTemplate = new RestTemplate();
                MultiValueMap<String, String> headers = getHeaders();

                Map<Object, Object> payload = new HashMap<>();
                HttpEntity<?> request = new HttpEntity<>(payload, headers);
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/balance", request, String.class);

                response = new String(apiCrypt.decryptByte(responseEntity.getBody())).trim();
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
        LoginResponseType loginResponseType = doLogin();

        if (loginResponseType == LoginResponseType.LOGIN_SUCCESS || loginResponseType == LoginResponseType.LOGIN_ALREADY_LOGGED_IN) {

            RestTemplate restTemplate = new RestTemplate();
            MultiValueMap<String, String> headers = getHeaders();

            String network = OnecardRechargeFactory.codeMapper.get(request.getServiceCode());

            RequestOnecardAirtimeDto dto = new RequestOnecardAirtimeDto(Integer.valueOf(network), request.getRecipient(), request.getServiceCost().intValue(), null, null);
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
            String encryptedRequest = Base64.getEncoder().encodeToString(apiCrypt.encryptByte(objectMapper.writeValueAsString(dto)));
            requestBody.add("request", encryptedRequest);

            HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(baseUrl + "/doPayment", HttpMethod.POST, formEntity, String.class);

            String decryptedResponse = new String(apiCrypt.decryptByte(response.getBody())).trim();
            log.info(decryptedResponse);

            GenericResponse genericResponse = objectMapper.readValue(decryptedResponse, GenericResponse.class);

            if (!genericResponse.isSuccess()) {
                return RechargeStatus.builder()
                        .message(genericResponse.getMessage())
                        .status(HttpStatus.BAD_REQUEST)
                        .results(genericResponse.getResults())
                        .build();
            }

            AirtimeRechargeResponse rechargeResponse = objectMapper.readValue(decryptedResponse, AirtimeRechargeResponse.class);

            return RechargeStatus.builder()
                    .message(rechargeResponse.getMessage())
                    .status(HttpStatus.OK)
                    .results("")
                    .build();
        }

        return RechargeStatus.builder()
                .message("Onecard API Airtime Recharge Failed")
                .results("Onecard API Airtime Recharge Failed")
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    private synchronized LoginResponseType doLogin() throws Exception {
        if (apiCrypt == null) {
            return login();
        } else if (expiry.before(new Date())) {
            logout();
            return login();
        }

        log.info("Already LoggedIn");
        return LoginResponseType.LOGIN_ALREADY_LOGGED_IN;
    }

    public void logout() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers = getHeaders();

        Map<Object, Object> payload = new HashMap<>();
        HttpEntity<?> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(baseUrl + "/logout", request, String.class);

        final String response = new String(apiCrypt.decryptByte(responseEntity.getBody())).trim();
        GenericResponse genericResponse = objectMapper.readValue(response, GenericResponse.class);
        log.info("Logout Response {}", genericResponse);

    }

    private LoginResponseType login() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String encryptedUser = Base64.getEncoder().encodeToString(loginCrypt.encryptByte(user));
        String encryptedPassword = Base64.getEncoder().encodeToString(loginCrypt.encryptByte(password));

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("username", encryptedUser);
        requestBody.add("pass", encryptedPassword);

        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(baseUrl + "/login", HttpMethod.POST, formEntity, String.class);

        String decrypt = new String(loginCrypt.decryptByte(response.getBody())).trim();

        LoginResponse loginResponse = objectMapper.readValue(decrypt, LoginResponse.class);

        if (loginResponse.isResponse() && loginResponse.getResponseCode() == 200) {
            userToken = loginResponse.getResponseData().userToken;
            authToken = loginResponse.getResponseData().authToken;

            MCrypt crypt = new MCrypt(userToken, iVSecret);

            String tempToken = new String(crypt.decryptByte(authToken)).trim();
            String[] split = tempToken.split("~");

            if (split.length != 2) {
                throw new RuntimeException("Split Failed");
            }

            apiCrypt = new MCrypt(userToken, split[1]);
            expiry = new Date(loginResponse.getResponseData().expireAt * 1000);

            return LoginResponseType.LOGIN_SUCCESS;
        }

        return LoginResponseType.LOGIN_FAILED;
    }

    @Override
    @Cacheable(CachingConfig.ONECARD_PLAN_CACHE)
    public List<DataPlanDto> getDataPlans(String requestCode) {
        log.info("Retrieving Data Plan for Onecard code {}", requestCode);

        String network = RingoRechargeFactory.codeMapper.get(requestCode);

        return null;
    }

    @Override
    public DataPlanDto getPlan(String id, String planCode) {
        return cacheProxy.getOnecardPlans(planCode)
                .stream()
                .filter(p -> p.getProduct_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Unable to find Plan %s in cached Onecard Plans", id)));
    }

    private MultiValueMap<String, String> getHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();

        map.put("Content-Type", "application/json");
        map.put("token", userToken);
        map.put("authtoken", authToken);

        headers.setAll(map);

        return headers;
    }
}
