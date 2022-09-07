package io.factorialsystems.msscapiuser.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscapiuser.dto.request.ExtraPlanRequestDto;
import io.factorialsystems.msscapiuser.dto.response.ExtraDataPlanDto;
import io.factorialsystems.msscapiuser.security.RestTemplateInterceptor;
import io.factorialsystems.msscapiuser.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/plans")
public class PlanController {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final SecurityService securityService;

    @Value("${api.host.baseurl}")
    private String baseUrl;

    @PostMapping
    public ResponseEntity<ExtraDataPlanDto> getExtraDataPlans(@Valid @RequestBody ExtraPlanRequestDto dto) throws JsonProcessingException {
        log.info("Extra Data Plan API Call");

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(dto), securityService.getHttpHeaders());
        return restTemplate.exchange (baseUrl + "api/v1/recharge/plans", HttpMethod.POST, request, ExtraDataPlanDto.class);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getDataPlans(@PathVariable("code") String code) {
        log.info("Data Plan API Call");

        RestTemplate localTemplate = new RestTemplate();
        localTemplate.getInterceptors().add(new RestTemplateInterceptor());

        return localTemplate.getForEntity(baseUrl + "api/v1/recharge/plans/" + code, List.class);
    }
}
