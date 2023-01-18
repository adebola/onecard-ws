package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.domain.CombinedRechargeList;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import io.factorialsystems.msscprovider.dto.report.RechargeReportRequestDto;
import io.factorialsystems.msscprovider.service.RechargeReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final RechargeReportService reportService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<CombinedRechargeList> runRechargeReport(@Valid @RequestBody RechargeReportRequestDto dto) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(reportService.runRechargeReport(dto));
    }

    @PostMapping("/short")
    public ResponseEntity<?> getShortProviderExpenditure(@Valid @RequestBody RechargeProviderRequestDto dto) {
        return ResponseEntity.ok(reportService.getShortRechargeExpenditure(dto));
    }

    @PostMapping("/long")
    public ResponseEntity<?> getLongProviderExpenditure(@Valid @RequestBody RechargeProviderRequestDto dto) {
        return null;
    }
}
