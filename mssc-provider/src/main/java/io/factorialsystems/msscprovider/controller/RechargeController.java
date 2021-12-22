package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.MessageDto;
import io.factorialsystems.msscprovider.dto.RechargeRequestDto;
import io.factorialsystems.msscprovider.service.RechargeService;
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
    private final RechargeService rechargeService;

    @PostMapping
    public ResponseEntity<RechargeRequestDto> startRecharge(@Valid @RequestBody RechargeRequestDto dto) {
        return new ResponseEntity<>(rechargeService.startRecharge(dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> finishRecharge(@PathVariable("id") Integer id) {

             if (rechargeService.finishRecharge(id)) {
                 return new ResponseEntity<>(new MessageDto("Recharge Successfully dispensed"), HttpStatus.OK);
             }

             return new ResponseEntity<>(new MessageDto("Recharge Failed"), HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/plans/{code}")
    public ResponseEntity<?> getDataPlans(@PathVariable("code") String code) {
        return new ResponseEntity<>(rechargeService.getDataPlans(code), HttpStatus.OK);
    }
}
