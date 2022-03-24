package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.MessageDto;
import io.factorialsystems.msscprovider.dto.RingoValidateCableRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeResponseDto;
import io.factorialsystems.msscprovider.recharge.RechargeResponseStatus;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.ringo.DstvService;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoFetchDstvAddonRequest;
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
@RequestMapping("/api/v1/recharge/cable")
public class CableRechargeController {
    private final SingleRechargeService rechargeService;
    private final DstvService dstvService;

    @PostMapping("/validate")
    @Operation(summary = "Validate Cable TV", description = "It validates users smart card against respective cable tv and returns details of such smart card account.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Validate/Retrieves Smart Card Details Successfully.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RechargeResponseStatus.class))})})
    public ResponseEntity<RechargeResponseStatus> validateDstv(@Valid @RequestBody RingoValidateCableRequestDto ringoValidateCableRequestDto) {
        return new ResponseEntity<>(dstvService.validateCable(ringoValidateCableRequestDto), HttpStatus.OK);
    }

    @GetMapping("/plans/{code}")
    @Operation(summary = "Retrieve Cable TV Addon", description = "It retrieves a particular cable tv list of add on based on the previously validated smart card account, meaning that code parsed here is gotten after calling validate endpoint.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Retrieves Addon Successfully.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RechargeResponseStatus.class))})})
    public ResponseEntity<RechargeResponseStatus> fetchAddonList(@PathVariable String code) {
        return new ResponseEntity<>(dstvService.fetchAddonList(new RingoFetchDstvAddonRequest(code)), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Starts Cable TV Payment", description = "It initiate a cable tv payment based on the previously validated smart card account details, meaning that code values parsed to the object is gotten after calling validate and or addon endpoints respectively.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Retrieves Addon Successfully.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RechargeResponseStatus.class))})})
    public ResponseEntity<SingleRechargeResponseDto> startRecharge(@Valid @RequestBody SingleRechargeRequestDto dto) {
        return new ResponseEntity<>(rechargeService.startRecharge(dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Finish/Complete Cable TV Payment", description = "It proceed to complete cable tv payment/subscription based on the previously initialised payment, meaning that 'id' parse is gotten after calling start/initialise payment.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Retrieves Addon Successfully.", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RechargeResponseStatus.class))})})
    public ResponseEntity<MessageDto> finishRecharge(@PathVariable("id") String id) {
        RechargeStatus status = rechargeService.finishRecharge(id);

        if (status == null || status.getMessage() == null) {
            return new ResponseEntity<>(new MessageDto("Recharge Failed"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new MessageDto(status.getMessage()), status.getStatus());
    }
}
