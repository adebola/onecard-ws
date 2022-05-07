package io.factorialsystems.msscaudit.controller;

import io.factorialsystems.msscaudit.dto.ContactMessageDto;
import io.factorialsystems.msscaudit.dto.PagedDto;
import io.factorialsystems.msscaudit.service.ContactService;
import io.factorialsystems.msscaudit.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contact")
public class ContactController {
    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<PagedDto<ContactMessageDto>> findAll(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                               @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(contactService.findAll(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactMessageDto> findById(@PathVariable("id") String id) {
        return new ResponseEntity<>(contactService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedDto<ContactMessageDto>> search(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                              @RequestParam(value = "searchString") String searchString) {
        return new ResponseEntity<>(contactService.search(pageNumber, pageSize, searchString), HttpStatus.OK);
    }
}
