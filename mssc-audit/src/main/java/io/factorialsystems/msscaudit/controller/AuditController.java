package io.factorialsystems.msscaudit.controller;

import io.factorialsystems.msscaudit.dto.AuditMessageDto;
import io.factorialsystems.msscaudit.dto.AuditSearchDto;
import io.factorialsystems.msscaudit.dto.PagedDto;
import io.factorialsystems.msscaudit.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/audit")
public class AuditController {
    private final MessageService messageService;

    @GetMapping
    @PreAuthorize("hasAnyRole('Onecard_Admin', 'Onecard_Audit', 'Onecard_Revenue_Assurance')")
    public ResponseEntity<PagedDto<AuditMessageDto>> findAll(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                     @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        return new ResponseEntity<>(messageService.findAll(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('Onecard_Admin', 'Onecard_Audit', 'Onecard_Revenue_Assurance')")
    public ResponseEntity<AuditMessageDto> findById(@PathVariable("id") String id) {
        return new ResponseEntity<>(messageService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('Onecard_Admin', 'Onecard_Audit', 'Onecard_Revenue_Assurance')")
    public ResponseEntity<PagedDto<AuditMessageDto>> search(@Valid @RequestBody AuditSearchDto dto,
                                                            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        return new ResponseEntity<>(messageService.search(pageNumber, pageSize, dto), HttpStatus.OK);
    }

    @PostMapping("/report")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('Onecard_Admin', 'Onecard_Audit', 'Onecard_Revenue_Assurance')")
    public List<AuditMessageDto> findUnPaged(@Valid @RequestBody AuditSearchDto dto) {
        return messageService.findAllUnPaged(dto);
    }

}
