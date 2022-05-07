package io.factorialsystems.msscaudit.controller;

import io.factorialsystems.msscaudit.dto.ContactMessageDto;
import io.factorialsystems.msscaudit.service.ContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contactus")
public class ContactUSController {
    private final ContactService contactService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void saveContact(@RequestBody @Valid ContactMessageDto dto) {
        contactService.saveContactMessage(dto);
    }
}
