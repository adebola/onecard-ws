package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.ServerResponse;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.service.telcos.RechargeServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/recharge")
public class TelcoRechargeControllerImpl implements TelcoController {
    private final RechargeServiceImpl rechargeService;

    @Override
    public ResponseEntity<ServerResponse> startRecharge(@Valid @RequestBody SingleRechargeRequestDto singleRechargeRequestDto) {
        ServerResponse serverResponse = rechargeService.startRecharge(singleRechargeRequestDto);
        return new ResponseEntity<>(serverResponse, serverResponse.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ServerResponse> completeRecharge(String rechargeRequestId) {
        ServerResponse serverResponse = rechargeService.completeRecharge(rechargeRequestId);
        return new ResponseEntity<>(serverResponse, serverResponse.getStatus() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<ServerResponse> getDataPlans(String code) {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
