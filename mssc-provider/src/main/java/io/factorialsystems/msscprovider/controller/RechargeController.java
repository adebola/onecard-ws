package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.MessageDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestResponseDto;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.service.SingleRechargeService;
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

    @PostMapping
    public ResponseEntity<SingleRechargeRequestResponseDto> startRecharge(@Valid @RequestBody SingleRechargeRequestDto dto) {
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
