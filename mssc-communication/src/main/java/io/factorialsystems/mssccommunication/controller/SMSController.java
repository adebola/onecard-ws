package io.factorialsystems.mssccommunication.controller;

import io.factorialsystems.mssccommunication.dto.BulkSMSMessageDto;
import io.factorialsystems.mssccommunication.dto.SMSMessageDto;
import io.factorialsystems.mssccommunication.service.sms.SMSMessageService;
import io.factorialsystems.mssccommunication.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sms")
public class SMSController {
    private final SMSMessageService smsService;

    @PostMapping
    public ResponseEntity sendSingleSMS(@RequestBody @Valid SMSMessageDto smsDto) {
        return  smsService.sendMessage(smsDto) ? new ResponseEntity<>(HttpStatus.ACCEPTED) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping
    public ResponseEntity<?> getAllSMSMessages(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                               @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(smsService.findAll(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/self")
    public ResponseEntity<?> getMySMSMessages(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(smsService.findByUserId(K.getUserId(), pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendBulkSMS(@RequestBody @Valid BulkSMSMessageDto bulkSMSMessageDto) {
        smsService.sendBulkMessages(bulkSMSMessageDto.getMessages());
    }
}
