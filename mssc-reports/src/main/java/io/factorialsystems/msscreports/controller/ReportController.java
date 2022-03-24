package io.factorialsystems.msscreports.controller;

import io.factorialsystems.msscreports.dto.MessageDto;
import io.factorialsystems.msscreports.dto.ReportDto;
import io.factorialsystems.msscreports.service.ReportService;
import io.factorialsystems.msscreports.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService reportService;

    @GetMapping
    public ResponseEntity<?> getAllReports(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        log.info("iss claim {}", K.getIssClaim());

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
    public ResponseEntity<?> createProvider(@Valid @RequestBody ReportDto dto) {

        Integer reportId = reportService.saveReport(K.getUserName(), dto);
        return new ResponseEntity<>(reportService.findReportById(reportId), HttpStatus.CREATED);
    }

    @GetMapping("/run/{id}")
    public ResponseEntity<InputStreamResource> runReport(@PathVariable("id") Integer id) {
        ByteArrayInputStream byteArrayInputStream = reportService.runReport(id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(byteArrayInputStream));
    }
}
