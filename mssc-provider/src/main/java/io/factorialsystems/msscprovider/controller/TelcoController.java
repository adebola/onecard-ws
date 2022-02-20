package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.ServerResponse;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface TelcoController {

    @PostMapping
    ResponseEntity<ServerResponse> startRecharge(@Valid @RequestBody SingleRechargeRequestDto dto);

    @GetMapping("/{id}")
    ResponseEntity<ServerResponse> completeRecharge(@PathVariable("id") String rechargeRequestId);

    @GetMapping("/plans/{code}")
    ResponseEntity<ServerResponse> getDataPlans(@PathVariable("code") String code);

}