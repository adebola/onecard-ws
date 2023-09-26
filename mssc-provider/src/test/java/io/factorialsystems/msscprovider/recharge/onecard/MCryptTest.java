package io.factorialsystems.msscprovider.recharge.onecard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.dto.recharge.DataPlanDto;
import io.factorialsystems.msscprovider.recharge.onecard.response.BalanceResponse;
import io.factorialsystems.msscprovider.recharge.onecard.response.LoginResponse;
import io.factorialsystems.msscprovider.recharge.onecard.response.OnecardDataPlanResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class MCryptTest {
    private static String userToken;
    private static String authToken;
    private static MCrypt apiCrypt;

    @Test
    void testGetBalance() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<?> request = getHttpEntity(new LinkedMultiValueMap<>());
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://api.onecardnigeria.com/rest/balance", request, String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        String response = new String(apiCrypt.decryptByte(responseEntity.getBody())).trim();
        log.info(response);
        BalanceResponse balanceResponse = objectMapper.readValue(response, BalanceResponse.class);

        assertThat(balanceResponse).isNotNull();
        assertThat(balanceResponse.getResponseData()).isNotNull();

        BigDecimal balance =  new BigDecimal((balanceResponse.getResponseData().getBalance()));
        log.info("balance is {}", balance);
    }

    @Test
    void testGetServices() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> request = getHttpEntity(new LinkedMultiValueMap<>());
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://api.onecardnigeria.com/rest/services", request, String.class);

        String response = new String(apiCrypt.decryptByte(responseEntity.getBody())).trim();
        log.info(response);
    }

    @Test
    void testGetProducts() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> request = getHttpEntity(new LinkedMultiValueMap<>());
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://api.onecardnigeria.com/rest/products", request, String.class);

        String response = new String(apiCrypt.decryptByte(responseEntity.getBody())).trim();
        log.info(response);
    }

    @Test
    void testGetProductByServiceId() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("token", userToken);
        headers.add("authtoken", authToken);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("service_id", Base64.getEncoder().encodeToString(apiCrypt.encryptByte("1")));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity( "https://api.onecardnigeria.com/rest/products", request , String.class );

        final String response = new String(apiCrypt.decryptByte(responseEntity.getBody())).trim();
        log.info(response);
    }

    @Test
    void testGetProductItems() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("token", userToken);
        headers.add("authtoken", authToken);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("product_id", Base64.getEncoder().encodeToString(apiCrypt.encryptByte("24")));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity( "https://api.onecardnigeria.com/rest/productitems", request , String.class );

        final String response = new String(apiCrypt.decryptByte(responseEntity.getBody())).trim();
        log.info(response);
    }

    @Test
    void testGetProductParams() throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("token", userToken);
        headers.add("authtoken", authToken);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

        requestBody.add("product_id", Base64.getEncoder().encodeToString(apiCrypt.encryptByte("24")));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity( "https://api.onecardnigeria.com/rest/params", request , String.class );

        final String response = new String(apiCrypt.decryptByte(responseEntity.getBody())).trim();

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode apiNode = objectMapper.readTree(response).path("RESPONSE_DATA").path("api_plans");
        OnecardDataPlanResponse[] plans = objectMapper.readValue(apiNode.toString(), OnecardDataPlanResponse[].class);

        final List<DataPlanDto> collect = Arrays.stream(plans)
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


        log.info("DataPlansDto {}", collect);
    }

//    @Test
//    void testRecharge() throws Exception {
//        RestTemplate restTemplate = new RestTemplate();
//        ObjectMapper objectMapper = new ObjectMapper();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//        headers.add("token", userToken);
//        headers.add("authtoken", authToken);
//
//        List<RequestOnecardAirtimeDto> dto = List.of(new RequestOnecardAirtimeDto(24, "08020893456", 100, null, null));
//        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
//        String encryptedRequest = Base64.getEncoder().encodeToString(apiCrypt.encryptByte(objectMapper.writeValueAsString(dto)));
//        requestBody.add("request", encryptedRequest);
//
//        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> response =
//                restTemplate.exchange ("https://api.onecardnigeria.com/rest/doPayment", HttpMethod.POST, formEntity, String.class);
//
//        String decryptedResponse = new String(apiCrypt.decryptByte(response.getBody())).trim();
//        //log.info(decryptedResponse);
//        AirtimeRechargeResponse rechargeResponse = objectMapper.readValue(decryptedResponse, AirtimeRechargeResponse.class);
//
//        log.info("RESPONSE {}", rechargeResponse);
//    }

    @Test
    void parseJson() throws JsonProcessingException {
        final String JsonStr = "{\"RESPONSE\":true,\"RESPONSE_MSG\":\"Login Successful\",\"RESPONSE_DATA\":{\"USER_TOKEN\":\"647a2bc1-ce4c-4203-99b1-41050a40\",\"AUTH_TOKEN\":\"HaBJfrjCdX7R7COoBIjZeWe5VB3jCdUcpkIyWa03WwRefCIDdw2BTYvbXIRN9mejMDtp0QS6ppYFDiisSRxQyQ==\",\"USER_ID\":{\"User\":{\"id\":\"137970\"}},\"EXPIRE_AT\":1685746193},\"RESPONSE_CODE\":200}";
        ObjectMapper objectMapper = new ObjectMapper();
        LoginResponse loginResponse = objectMapper.readValue(JsonStr, LoginResponse.class);
        log.info("Response {}", loginResponse);
    }

    @Test
    void decryptString() throws Exception {
        final String id = "zKnY1ZcAA3f0QefQTC7nXQQZSRfxkPb6c9ZCzP1h6axdr86kaf5o4stsNJNDcWWhamcEX5qZ\\/U3rDECmARjjow==";
        log.info(id);

        MCrypt mCrypt = new MCrypt("647d8af4-87e4-4f68-93b2-45e10a40", "22d2b48279af3df6");
        String s = new String(mCrypt.decryptByte(id)).trim();

        log.info(s);
    }

    private HttpEntity<?> getHttpEntity(MultiValueMap<String, String> payload) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        Map<String, String> map = new HashMap<>();

        map.put("Content-Type", "application/json");
        map.put("token", userToken);
        map.put("authtoken", authToken);

        headers.setAll(map);

        return new HttpEntity<>(payload, headers);
    }

    @BeforeAll
    static void setup() throws Exception {
        final String loginUrl = "https://api.onecardnigeria.com/rest/login";
        MCrypt mCrypt = new MCrypt("61f22f21-1fdc-45f1-acee-2a8a2bfc", "22d2b48279af3df6");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//        String encryptedUser = Base64.getEncoder()
//                .encodeToString(mCrypt.encryptByte("onecard-dev-mat"));
        String encryptedUser = Base64.getEncoder()
                .encodeToString(mCrypt.encryptByte("onecard"));
        String encryptedPassword = Base64.getEncoder()
                .encodeToString(mCrypt.encryptByte("password"));

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("username", encryptedUser);
        requestBody.add("pass", encryptedPassword);

        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response =
                restTemplate.exchange (loginUrl, HttpMethod.POST, formEntity, String.class);

        log.info(response.getBody());

        String  decrypt = new String(mCrypt.decryptByte(response.getBody())).trim();
        log.info(decrypt);

        ObjectMapper objectMapper = new ObjectMapper();
        LoginResponse loginResponse = objectMapper.readValue(decrypt, LoginResponse.class);
        log.info("Response {}", loginResponse);

        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.isResponse()).isEqualTo(true);

        userToken = loginResponse.getResponseData().userToken;
        authToken = loginResponse.getResponseData().authToken;



        Date date = new Date(loginResponse.getResponseData().expireAt * 1000);

//        long microseconds = loginResponse.getResponseData().expireAt / 1_000;
//        Instant instant = fromNanos(microseconds);
        log.info("DATE {}", date);



        log.info("UserToken {}", userToken);
        log.info("AuthToken {}", authToken);

        MCrypt crypt = new MCrypt(userToken, "22d2b48279af3df6");

        String tempToken = new String(crypt.decryptByte(authToken)).trim();
        String[] split = tempToken.split("~");

        apiCrypt = new MCrypt(userToken, split[1]);
    }
}

