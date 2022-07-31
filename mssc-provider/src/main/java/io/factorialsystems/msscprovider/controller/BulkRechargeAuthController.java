package io.factorialsystems.msscprovider.controller;


import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.service.bulkrecharge.NewBulkRechargeService;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

        return new ResponseEntity<>(newBulkRechargeService.getUserRecharges(K.getUserId(), pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/list/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getAdminUserBulkRecharges(@PathVariable("id") String id,
                                                       @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                       @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getUserRecharges(id, pageNumber, pageSize), HttpStatus.OK);
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

    @PostMapping("/adminsearch")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> adminSearch(@Valid @RequestBody SearchBulkRechargeDto dto) {
        return new ResponseEntity<>(newBulkRechargeService.search(dto), HttpStatus.OK);
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

    @PostMapping("/individual/search")
    public ResponseEntity<?> searchIndividualByStatus(@Valid @RequestBody SearchIndividualDto dto,
                                                      @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.searchIndividual(dto, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> generateExcelFile(@PathVariable("id") String id) {
        final String filename = String.format("%s.%s", id, "xlsx");

        InputStreamResource file = new InputStreamResource(newBulkRechargeService.generateExcelFile(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @GetMapping("/individualretry/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> retryFailedRequest(@PathVariable("id") Integer id) {
        return new ResponseEntity<>(newBulkRechargeService.retryFailedRecharge(id), HttpStatus.OK);
    }

    @GetMapping("/bulkretry/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public void retryFailedRequests(@PathVariable("id") String id) {
        newBulkRechargeService.asyncRetryFailedRecharges(id);
    }


    @GetMapping("/individualrefund/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public void refundFailedRequest(@PathVariable("id") Integer id,
                                    @RequestParam(value = "bulkId", required = true) String bulkId) {
        newBulkRechargeService.refundFailedRecharge(id, bulkId);
    }

    @GetMapping("/bulkrefund/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public void refundFailedRequests(@PathVariable("id") String id) {
        newBulkRechargeService.refundFailedRecharges(id);
    }

    @PutMapping("/bulkresolve/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> resolveFailedRequests(@PathVariable("id") String id, @Valid @RequestBody ResolveRechargeDto dto) {
        return new ResponseEntity<>(newBulkRechargeService.resolveRecharges(id, dto), HttpStatus.OK);
    }

    @PutMapping("/individualresolve/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> resolveFailedRequest(@PathVariable("id") String id, @Valid @RequestBody ResolveRechargeDto dto) {
        return new ResponseEntity<>(newBulkRechargeService.resolveRecharge(id, dto), HttpStatus.OK);
    }

    @GetMapping("/failed")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getFailedRecharges(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getFailedRequests(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/failedunresolved")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getFailedUnresolvedRecharges(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getFailedUnresolvedRequests(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/failed/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getFailedIndividuals(@PathVariable("id") String id,
                                                  @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getFailedIndividuals(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/failedunresolved/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getFailedUnresolvedIndividuals(@PathVariable("id") String id,
                                                  @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getFailedUnresolvedIndividuals(id, pageNumber, pageSize), HttpStatus.OK);
    }
}
