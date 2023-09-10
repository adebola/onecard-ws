package io.factorialsystems.msscwallet.service;

import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@CommonsLog
public class KycServiceNoAuthTest {

    @Test
    void testKYC() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        headers.set("x-api-key", "x-api-key");
        headers.set("app-id", "app-id");

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("number", "22225250950");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

//        String response =
//                restTemplate.postForObject("https://api.prembly.com/identitypass/verification/bvn_validation", entity, String.class);
//
//        log.info(response);
    }
}


