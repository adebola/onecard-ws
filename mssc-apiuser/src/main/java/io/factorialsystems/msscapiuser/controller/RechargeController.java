package io.factorialsystems.msscapiuser.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscapiuser.dto.request.SingleRechargeRequestDto;
import io.factorialsystems.msscapiuser.dto.response.SingleRechargeResponseDto;
import io.factorialsystems.msscapiuser.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/single-recharge")
public class RechargeController {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final SecurityService securityService;

    @Value("${api.host.baseurl}")
    private String baseUrl;

    @PostMapping
    public ResponseEntity<SingleRechargeResponseDto> startRecharge(@RequestBody SingleRechargeRequestDto dto) throws JsonProcessingException {
        log.info("Single Recharge API Call");

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(dto), securityService.getHttpHeaders());
        return restTemplate.exchange (baseUrl + "api/v1/auth-recharge", HttpMethod.POST, request, SingleRechargeResponseDto.class);
    }
}
