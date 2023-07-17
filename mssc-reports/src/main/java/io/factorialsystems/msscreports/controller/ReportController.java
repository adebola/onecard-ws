package io.factorialsystems.msscreports.controller;

import io.factorialsystems.msscreports.dto.*;
import io.factorialsystems.msscreports.service.ReportService;
import io.factorialsystems.msscreports.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService reportService;

    private static final String EXCEL_MEDIA_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @GetMapping
    public ResponseEntity<?> getAllReports(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(reportService.findReports(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchReports(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                           @RequestParam(value = "searchString") String searchString) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(reportService.searchReports(pageNumber, pageSize, searchString), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getReport(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(reportService.findReportById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateReport(@PathVariable("id") Integer id, @Valid @RequestBody ReportDto dto) {
        reportService.updateReport(id, dto);
        return new ResponseEntity<>(new MessageDto("Success"), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createReport(@Valid @RequestBody ReportDto dto) {

        Integer reportId = reportService.saveReport(K.getUserName(), dto);
        return new ResponseEntity<>(reportService.findReportById(reportId), HttpStatus.CREATED);
    }

    @PostMapping("/recharge")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<Resource> runRechargeReport(@Valid @RequestBody RechargeReportRequestDto dto) {
        final String filename = String.format("recharge-%s.xlsx", UUID.randomUUID());

        log.info("Running Recharge Report Parameters {}", dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
                .body(reportService.runRechargeReport(dto));
    }

    @PostMapping("/wallet")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<Resource> runWalletReport(@Valid @RequestBody WalletReportRequestDto dto) {
        final String filename = String.format("wallet-%s.xlsx", UUID.randomUUID());

        log.info("Running User Wallet Report Parameters {}", dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
                .body(reportService.runWalletReport(dto));
    }

    @PostMapping("/audit")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('Onecard_Admin', 'Onecard_Audit', 'Onecard_Revenue_Assurance')")
    public ResponseEntity<Resource> runAuditReport(@Valid @RequestBody AuditSearchDto auditSearchDto) {
        final String filename = String.format("audit-%s.xlsx", UUID.randomUUID());
        log.info("Running Audit Report Parameters {}", auditSearchDto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
                .body(reportService.runAuditReport(auditSearchDto));
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('Onecard_Admin', 'Onecard_Audit', 'Onecard_Revenue_Assurance')")
    public ResponseEntity<Resource> runUserReport() {
        final String filename = "all-user-report.xlsx";
        log.info("Running All Users Report");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
                .body(reportService.runUserReport());
    }
}
