package io.factorialsystems.mssccommunication.controller;

import io.factorialsystems.mssccommunication.dto.MailMessageDto;
import io.factorialsystems.mssccommunication.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mail")
public class MailController {
    private final MailService mailService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String sendMailWithoutAttachment(@Valid @RequestBody MailMessageDto dto)  {
        return mailService.sendMail(dto, null);
    }

//    @PostMapping( value = "/attachment", consumes = {MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE})
    @PostMapping("/attachment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String sendMailWithAttachment(@RequestPart(value = "message") @Valid MailMessageDto dto, @RequestPart(value = "file") MultipartFile file)  {
        return mailService.sendMail(dto, file);
    }
}
