package io.factorialsystems.msscapiuser.controller;

import io.factorialsystems.msscapiuser.dto.response.BalanceDto;
import io.factorialsystems.msscapiuser.security.RestTemplateInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/balance")
public class BalanceController {
    @Value("${api.host.baseurl}")
    private String baseUrl;

    @GetMapping
    public ResponseEntity<BalanceDto> getUserBalance() {
        RestTemplate localTemplate = new RestTemplate();
        localTemplate.getInterceptors().add(new RestTemplateInterceptor());

        return localTemplate.getForEntity(baseUrl + "api/v1/account/balance", BalanceDto.class);
    }
}
