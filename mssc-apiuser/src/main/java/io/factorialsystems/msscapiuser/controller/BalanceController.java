package io.factorialsystems.msscapiuser.controller;

import io.factorialsystems.msscapiuser.dto.response.BalanceDto;
import io.factorialsystems.msscapiuser.security.RestTemplateInterceptor;
import io.factorialsystems.msscapiuser.service.SecurityService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private final SecurityService securityService;

    @Value("${api.host.baseurl}")
    private String baseUrl;


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BalanceDto.class))})
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-TOKEN", value = "Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "supplied_token"),
            @ApiImplicitParam(name = "X-SECRET", value = "Secret", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "supplied_secret")
    })
    @Operation(summary = "Get Balance on User account", description = "Get balance on User account")
    @GetMapping
    public ResponseEntity<BalanceDto> getUserBalance() {
        log.info("Calling Balance API for {}", securityService.getUserName());
        RestTemplate localTemplate = new RestTemplate();
        localTemplate.getInterceptors().add(new RestTemplateInterceptor());

        return localTemplate.getForEntity(baseUrl + "api/v1/account/balance", BalanceDto.class);
    }
}
