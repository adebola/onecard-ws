package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.service.BulkRechargeService;
import io.factorialsystems.msscprovider.service.ScheduledRechargeService;
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
@RequestMapping("/api/v1/auth-recharge")
public class RechargeAuthController {
    private final SingleRechargeService rechargeService;
    private final BulkRechargeService bulkRechargeService;
    private final ScheduledRechargeService scheduledRechargeService;

    @PostMapping
    public ResponseEntity<SingleRechargeRequestResponseDto> startRecharge(@Valid @RequestBody SingleRechargeRequestDto dto) {
        return new ResponseEntity<>(rechargeService.startRecharge(dto), HttpStatus.OK);
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkRechargeRequestResponseDto> startBulkRecharge(@Valid @RequestBody BulkRechargeRequestDto dto) {
        return new ResponseEntity<>(bulkRechargeService.saveService(dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> finishRecharge(@PathVariable("id") String id) {
        RechargeStatus status = rechargeService.finishRecharge(id);

        if (status == null || status.getMessage() == null) {
            return new ResponseEntity<>(new MessageDto("Recharge Failed"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new MessageDto(status.getMessage()), status.getStatus());
    }

    @GetMapping("/bulk/{id}")
    public ResponseEntity<MessageDto> finishBulkRecharge(@PathVariable("id") String id) {
        bulkRechargeService.asyncBulkRecharge(id);
        return new ResponseEntity<>(new MessageDto("Request submitted"), HttpStatus.OK);
    }

    @PostMapping("/scheduled")
    public ResponseEntity<?> startScheduledRecharge(@Valid @RequestBody ScheduledRechargeRequestDto dto) {
        return new ResponseEntity<>(scheduledRechargeService.startRecharge(dto), HttpStatus.OK);
    }

    @GetMapping("/scheduled/{id}")
    public ResponseEntity<MessageDto> finishScheduledRecharge(@PathVariable("id") String id) {
        return null;
    }
}
