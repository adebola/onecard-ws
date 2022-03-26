package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.MessageDto;
import io.factorialsystems.msscprovider.dto.RingoValidateCableRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeResponseDto;
import io.factorialsystems.msscprovider.recharge.RechargeResponseStatus;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.ringo.DstvService;
import io.factorialsystems.msscprovider.service.SingleRechargeService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recharge")
public class RechargeController {
    private final SingleRechargeService rechargeService;
    private final DstvService dstvService;

    @PostMapping("/validate")
    @Operation(summary = "Validate Cable TV", description = "It validates users smart card against respective cable tv and returns details of such smart card account.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Validate/Retrieves Smart Card Details Successfully.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RechargeResponseStatus.class))})})
    public ResponseEntity<RechargeResponseStatus> validateDstv(@Valid @RequestBody RingoValidateCableRequestDto ringoValidateCableRequestDto) {
        return new ResponseEntity<>(dstvService.validateCable(ringoValidateCableRequestDto), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<SingleRechargeResponseDto> startRecharge(@Valid @RequestBody SingleRechargeRequestDto dto) {
        return new ResponseEntity<>(rechargeService.startRecharge(dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> finishRecharge(@PathVariable("id") String id) {
        RechargeStatus status = rechargeService.finishRecharge(id);

        if (status == null || status.getMessage() == null) {
            return new ResponseEntity<>(new MessageDto("Recharge Failed"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new MessageDto(status.getMessage()), status.getStatus());
    }

    @GetMapping("/plans/{code}")
    public ResponseEntity<?> getDataPlans(@PathVariable("code") String code) {
        return new ResponseEntity<>(rechargeService.getDataPlans(code), HttpStatus.OK);
    }
}
