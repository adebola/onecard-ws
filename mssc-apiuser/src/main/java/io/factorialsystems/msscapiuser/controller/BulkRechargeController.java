package io.factorialsystems.msscapiuser.controller;

import io.factorialsystems.msscapiuser.dto.request.NewBulkRechargeRequestDto;
import io.factorialsystems.msscapiuser.dto.response.NewBulkRechargeResponseDto;
import io.factorialsystems.msscapiuser.external.client.ProviderClient;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/bulk-recharge")
public class BulkRechargeController {
    private final ProviderClient providerClient;
    private final SecurityService securityService;

    @Value("${api.host.baseurl}")
    private String baseUrl;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = NewBulkRechargeResponseDto.class))})
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-TOKEN", value = "Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "supplied_token"),
            @ApiImplicitParam(name = "X-SECRET", value = "Secret", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "supplied_secret")
    })
    @Operation(summary = "Submit a Bulk Recharge Request", description = "User Must have enough balance to cover the request otherwise it will fail")
    @PostMapping
    public ResponseEntity<NewBulkRechargeResponseDto> startNewBulkRecharge(@Valid @RequestBody NewBulkRechargeRequestDto dto) {
        log.info("BulkRecharge For User {}, Payload {}", securityService.getUserName(), dto);
        return ResponseEntity.ok(providerClient.bulkRecharge(dto));
    }
}
