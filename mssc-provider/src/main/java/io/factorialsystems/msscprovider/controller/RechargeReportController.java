package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import io.factorialsystems.msscprovider.service.RechargeReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recharge-report")
public class RechargeReportController {
    private RechargeReportService reportService;

    @PostMapping("/short")
    public ResponseEntity<?> getShortProviderExpenditure(@Valid @RequestBody RechargeProviderRequestDto dto) {
        return ResponseEntity.ok(reportService.getShortRechargeExpenditure(dto));
    }

    @PostMapping("/long")
    public ResponseEntity<?> getLongProviderExpenditure(@Valid @RequestBody RechargeProviderRequestDto dto) {
        return null;
    }
}
