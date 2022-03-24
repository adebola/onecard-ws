package io.factorialsystems.msscapiuser.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscapiuser.dto.request.SingleRechargeRequestDto;
import io.factorialsystems.msscapiuser.dto.response.SingleRechargeResponseDto;
import io.factorialsystems.msscapiuser.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth-recharge")
public class RechargeController {

    private final ObjectMapper objectMapper;

    @Value("${api.host.baseurl}")
    private String baseUrl;

    @PostMapping
    public ResponseEntity<SingleRechargeResponseDto> startRecharge(@RequestBody SingleRechargeRequestDto dto) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(TenantContext.getToken());
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(dto), headers);

        return restTemplate.exchange (baseUrl + "api/v1/auth-recharge", HttpMethod.POST, request, SingleRechargeResponseDto.class);
    }
}
