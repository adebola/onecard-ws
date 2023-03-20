package io.factorialsystems.msscaudit.controller;

import io.factorialsystems.msscaudit.dto.AuditMessageDto;
import io.factorialsystems.msscaudit.service.MessageService;
import io.factorialsystems.msscaudit.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit")
public class AuditController {
    private final MessageService messageService;

    @GetMapping
    @PreAuthorize("hasAnyRole('Onecard_Admin', 'Onecard_Audit', 'Onecard_Revenue_Assurance')")
    public ResponseEntity<?> findAll(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                     @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(messageService.findAll(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Onecard_Admin', 'Onecard_Audit', 'Onecard_Revenue_Assurance')")
    public ResponseEntity<AuditMessageDto> findById(@PathVariable("id") String id) {
        return new ResponseEntity<>(messageService.findById(id), HttpStatus.OK);
    }
}
