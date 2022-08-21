package io.factorialsystems.msscprovider.controller;


import io.factorialsystems.msscprovider.dto.DateDto;
import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.dto.recharge.NewScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.service.NewScheduledRechargeService;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping("/searchdate")
    public ResponseEntity<?> searchScheduledDate(@Valid @RequestBody DateDto dto,
                                                 @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newScheduledRechargeService.searchByDate(dto.getScheduledDate(), pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> generateExcelFile(@PathVariable("id") String id) {
        final String filename = String.format("%s.%s", id, "xlsx");

        InputStreamResource file = new InputStreamResource(newScheduledRechargeService.generateExcelFile(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);

    }
}
