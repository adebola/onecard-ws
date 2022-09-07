package io.factorialsystems.msscapiuser.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscapiuser.dto.request.NewBulkRechargeRequestDto;
import io.factorialsystems.msscapiuser.dto.response.NewBulkRechargeResponseDto;
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

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/bulk-recharge")
public class BulkRechargeController {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SecurityService securityService;

    @Value("${api.host.baseurl}")
    private String baseUrl;

    @PostMapping
    public ResponseEntity<NewBulkRechargeResponseDto> startNewBulkRecharge(@Valid @RequestBody NewBulkRechargeRequestDto dto) throws JsonProcessingException {
        log.info("Bulk Recharge API Call");

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(dto), securityService.getHttpHeaders());
        return restTemplate.exchange (baseUrl + "api/v1/auth-recharge/bulk", HttpMethod.POST, request, NewBulkRechargeResponseDto.class);

    }
}
