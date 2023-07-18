package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.domain.CombinedRechargeList;
import io.factorialsystems.msscprovider.dto.RechargeProviderExpenditure;
import io.factorialsystems.msscprovider.dto.provider.RechargeProviderDto;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import io.factorialsystems.msscprovider.dto.report.RechargeReportRequestDto;
import io.factorialsystems.msscprovider.service.RechargeProviderService;
import io.factorialsystems.msscprovider.service.RechargeReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recharge-report")
public class RechargeReportController {
    private final RechargeReportService reportService;
    private final RechargeProviderService rechargeProviderService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<CombinedRechargeList> runRechargeReport(@Valid @RequestBody RechargeReportRequestDto dto) {

        long startTime = System.nanoTime();
        CombinedRechargeList results  = reportService.runRechargeReport(dto);
        long elapsedTime = (System.nanoTime() - startTime) / 1_000_000;

        log.info("Total execution time of Recharge Report in milliseconds {}, Parameters {}", elapsedTime, dto);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(results);
    }

    @GetMapping("/balances")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public List<RechargeProviderDto> getProviderBalances() {
        return rechargeProviderService.findAllWithBalances();
    }

    @PostMapping("/short")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<List<RechargeProviderExpenditure>> getShortProviderExpenditure(@Valid @RequestBody RechargeProviderRequestDto dto) {
        return ResponseEntity.ok(reportService.getShortRechargeExpenditure(dto));
    }

    @PostMapping("/long")
    public ResponseEntity<List<RechargeProviderExpenditure>> getLongProviderExpenditure(@Valid @RequestBody RechargeProviderRequestDto dto) {
        return ResponseEntity.ok(reportService.getLongRechargeExpenditure(dto));
    }
}
