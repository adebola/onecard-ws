package io.factorialsystems.msscprovider.controller;


import io.factorialsystems.msscprovider.dto.DateDto;
import io.factorialsystems.msscprovider.dto.MessageDto;
import io.factorialsystems.msscprovider.dto.NewBulkRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.NewBulkRechargeResponseDto;
import io.factorialsystems.msscprovider.service.NewBulkRechargeService;
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
@RequestMapping("/api/v1/auth-recharge/bulk")
public class BulkRechargeAuthController {
    private final NewBulkRechargeService newBulkRechargeService;

    @PostMapping
    public ResponseEntity<NewBulkRechargeResponseDto> startNewBulkRecharge(@Valid @RequestBody NewBulkRechargeRequestDto dto) {
        return new ResponseEntity<>(newBulkRechargeService.saveService(dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> finishNewBulkRecharge(@PathVariable("id") String id) {
        newBulkRechargeService.asyncRecharge(id);
        return new ResponseEntity<>(new MessageDto("Request submitted successfully"), HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadBulkFile(@RequestPart(value = "file") MultipartFile file) {
        newBulkRechargeService.uploadRecharge(file);
        return new ResponseEntity<>(new MessageDto("Bulk Request has been submitted successfully, results will be mailed to you"), HttpStatus.ACCEPTED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUserBulkRecharges(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getUserRecharges(pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/searchdate")
    public ResponseEntity<?> searchBulkByDate(@Valid @RequestBody DateDto dto,
                                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.searchByDate(dto.getScheduledDate(), pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/individual/{id}")
    public ResponseEntity<?> getBulkIndividualRequest(@PathVariable("id") String id,
                                                      @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getBulkIndividualRequests(id, pageNumber, pageSize), HttpStatus.OK);
    }
}
