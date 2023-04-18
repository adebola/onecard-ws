package io.factorialsystems.msscapiuser.controller;

import io.factorialsystems.msscapiuser.dto.request.ExtraPlanRequestDto;
import io.factorialsystems.msscapiuser.dto.response.DataPlanDto;
import io.factorialsystems.msscapiuser.dto.response.ExtraDataPlanDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/plans")
public class PlanController {
    private final ProviderClient providerClient;
    private final SecurityService securityService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExtraDataPlanDto.class))})
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-TOKEN", value = "Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "supplied_token"),
            @ApiImplicitParam(name = "X-SECRET", value = "Secret", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "supplied_secret"),
    })
    @Operation(summary = "Get Extra Data Plans", description = "Get extra plans for a specific user, applicable to dstv, electricity etc")
    @PostMapping
    public ResponseEntity<ExtraDataPlanDto> getExtraDataPlans(@Valid @RequestBody ExtraPlanRequestDto dto) {
        log.info("Extra Data Plan API For {}, Payload {}", securityService.getUserName(), dto);
        return ResponseEntity.ok(providerClient.getExtraDataPlans(dto));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DataPlanDto.class))})
    })
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-TOKEN", value = "Token", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "supplied_token"),
            @ApiImplicitParam(name = "X-SECRET", value = "Secret", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = String.class, example = "supplied_secret"),
            @ApiImplicitParam(name = "code", value = "code", required = true, allowEmptyValue = false, paramType = "path", dataTypeClass = String.class, example = "GLO-AIRTIME")
    })
    @Operation(summary = "Get Data Plans", description = "Get data plans for a service code")
    @GetMapping("/{code}")
    @ResponseStatus(HttpStatus.OK)
    public List<DataPlanDto> getDataPlans(@PathVariable("code") String code) {
        log.info("Data Plan API For {}, code {}", securityService.getUserName(), code);
        return providerClient.getDataPlans(code);
    }
}
