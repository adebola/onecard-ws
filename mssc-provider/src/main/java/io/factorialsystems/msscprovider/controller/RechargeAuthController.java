package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.service.SingleRechargeService;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth-recharge")
public class RechargeAuthController {
    private final SingleRechargeService rechargeService;

    @PostMapping
    public ResponseEntity<SingleRechargeResponseDto> startRecharge(@Valid @RequestBody SingleRechargeRequestDto dto) {
        return new ResponseEntity<>(rechargeService.startRecharge(dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> finishRecharge(@PathVariable("id") String id) {
        RechargeStatus status = rechargeService.finishRecharge(
                AsyncRechargeDto.builder()
                        .id(id)
                        .email(K.getEmail())
                        .build()
        );

        if (status == null || status.getMessage() == null) {
            return new ResponseEntity<>(new MessageDto("Recharge Failed"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new MessageDto(status.getMessage()), status.getStatus());
    }

    @PutMapping("/retry/{id}")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> retryRecharge(@PathVariable("id") String id,
                                           @RequestParam(value = "recipient", required = false) String recipient) {
        return new ResponseEntity<>(rechargeService.retryRecharge(id, recipient), HttpStatus.OK);
    }

    @PutMapping("/refund/{id}")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> refundRecharge(@PathVariable("id") String id) {
        return new ResponseEntity<>(rechargeService.refundRecharge(id), HttpStatus.OK);
    }

    @PutMapping("/resolve/{id}")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> refundRecharge(@PathVariable("id") String id, @Valid @RequestBody ResolveRechargeDto dto) {
        return new ResponseEntity<>(rechargeService.resolveRecharge(id, dto), HttpStatus.OK);
    }

    @GetMapping("/singlelist")
    public ResponseEntity<?> getUserSingleRecharges(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }
        return new ResponseEntity<>(rechargeService.getUserRecharges(K.getUserId(), pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/singlelist/{id}")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> getUserSingleRecharges(@PathVariable("id") String id,
                                                    @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }
        return new ResponseEntity<>(rechargeService.getUserRecharges(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/single/{id}")
    public ResponseEntity<?> getSingleRequest(@PathVariable("id") String id) {
        return new ResponseEntity<>(rechargeService.getRecharge(id), HttpStatus.OK);
    }

    @GetMapping("/single/searchrecipient")
    public ResponseEntity<?> searchSingleByRecipient(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                     @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                     @RequestParam(value = "searchString") String searchString) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(rechargeService.search(searchString, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/single/adminsearch")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> adminSearch(@Valid @RequestBody SearchSingleRechargeDto dto) {

        return new ResponseEntity<>(rechargeService.adminSearch(dto), HttpStatus.OK);
    }
}