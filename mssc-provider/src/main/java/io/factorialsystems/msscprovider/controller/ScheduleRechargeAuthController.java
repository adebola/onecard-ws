package io.factorialsystems.msscprovider.controller;


import io.factorialsystems.msscprovider.dto.DateDto;
import io.factorialsystems.msscprovider.dto.MessageDto;
import io.factorialsystems.msscprovider.dto.NewScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.service.NewScheduledRechargeService;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth-recharge/scheduled")
public class ScheduleRechargeAuthController {
    private final NewScheduledRechargeService newScheduledRechargeService;

    @PostMapping
    public ResponseEntity<?> startNewScheduledRecharge(@Valid @RequestBody NewScheduledRechargeRequestDto dto) {
        return new ResponseEntity<>(newScheduledRechargeService.startRecharge(dto), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> finishNewScheduledRecharge(@PathVariable("id") String id) {
        if (newScheduledRechargeService.finalizeScheduledRecharge(id)) {
            return new ResponseEntity<>(new MessageDto("Scheduled Recharge Request Submitted Successfully"), HttpStatus.ACCEPTED);
        } else {
            return new ResponseEntity<>(new MessageDto("Payment Failed for Scheduled Request"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadScheduleFile(@RequestPart(value = "date") DateDto dto, @RequestPart(value = "file") MultipartFile file) {
        newScheduledRechargeService.uploadRecharge(file, dto.getScheduledDate());
        return new ResponseEntity<>(new MessageDto("Scheduled Request has been submitted successfully, results will be mailed to you"), HttpStatus.ACCEPTED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUserScheduleRecharges(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newScheduledRechargeService.getUserRecharges(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/individual/{id}")
    public ResponseEntity<?> getScheduleBulkIndividualRequest(@PathVariable("id") String id,
                                                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newScheduledRechargeService.getBulkIndividualRequests(id, pageNumber, pageSize), HttpStatus.OK);
    }

}
