package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.MessageDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeResponseDto;
import io.factorialsystems.msscprovider.mapper.recharge.RechargeMapstructMapper;
import io.factorialsystems.msscprovider.recharge.RechargeResponseStatus;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.recharge.ringo.DstvService;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoFetchDstvAddonRequest;
import io.factorialsystems.msscprovider.recharge.ringo.request.RingoValidateDstvRequest;
import io.factorialsystems.msscprovider.service.SingleRechargeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recharge/dstv")
@Api(tags = "DSTV Service Management")
public class DstvRechargeController {
    private final SingleRechargeService rechargeService;
    private final DstvService dstvService;
    private final RechargeMapstructMapper rechargeMapstructMapper;

    @GetMapping("/doc")
    public String demo() {
        return "Hello World";
    }

    @GetMapping("/swagger-ui.html")
    public RedirectView redirectWithUsingRedirectView() {
        return new RedirectView("/swagger-ui/");
    }

    @PostMapping("/validate")
    @ApiOperation(value = "Validates DSTV Account Details", response = RechargeResponseStatus.class)
    public ResponseEntity<RechargeResponseStatus> validateDstv(@Valid @RequestBody RingoValidateDstvRequest ringoValidateDstvRequest) {
        return new ResponseEntity<>(dstvService.validateDstv(ringoValidateDstvRequest), HttpStatus.OK);
    }

    @GetMapping("/plans/{code}")
    @ApiOperation(value = "Fetch DSTV Addon List base on code from @validateDstv API response", response = RechargeResponseStatus.class)
    public ResponseEntity<RechargeResponseStatus> fetchAddonList(@PathVariable("code") String code) {
        return new ResponseEntity<>(dstvService.fetchAddonList(new RingoFetchDstvAddonRequest(code)), HttpStatus.OK);
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
}
