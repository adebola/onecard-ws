package io.factorialsystems.msscprovider.controller;


import io.factorialsystems.msscprovider.dto.DateDto;
import io.factorialsystems.msscprovider.dto.DateRangeDto;
import io.factorialsystems.msscprovider.dto.RechargeRequestStatusDto;
import io.factorialsystems.msscprovider.dto.ResolveRechargeDto;
import io.factorialsystems.msscprovider.dto.recharge.NewBulkRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.NewBulkRechargeResponseDto;
import io.factorialsystems.msscprovider.dto.search.SearchBulkFailedRechargeDto;
import io.factorialsystems.msscprovider.dto.search.SearchBulkRechargeDto;
import io.factorialsystems.msscprovider.dto.search.SearchIndividualDto;
import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.service.bulkrecharge.NewBulkRechargeService;
import io.factorialsystems.msscprovider.service.bulkrecharge.helper.BulkRechargeExcelGenerator;
import io.factorialsystems.msscprovider.utils.Constants;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth-recharge/bulk")
public class BulkRechargeAuthController {
    private final BulkRechargeExcelGenerator excelGenerator;
    private final NewBulkRechargeService newBulkRechargeService;

    @PostMapping
    public ResponseEntity<NewBulkRechargeResponseDto> startNewBulkRecharge(@Valid @RequestBody NewBulkRechargeRequestDto dto) {
        NewBulkRechargeResponseDto response = newBulkRechargeService.saveService(dto, Optional.empty());
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
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
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getUserRecharges(ProviderSecurity.getUserId(), pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/list/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getAdminUserBulkRecharges(@PathVariable("id") String id,
                                                       @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                       @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getUserRecharges(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/searchdate")
    public ResponseEntity<?> searchBulkByDate(@Valid @RequestBody DateDto dto,
                                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.searchByDate(dto.getScheduledDate(), pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/adminsearch")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> adminSearch(@Valid @RequestBody SearchBulkRechargeDto dto) {
        return new ResponseEntity<>(newBulkRechargeService.search(dto), HttpStatus.OK);
    }

    @PostMapping("/individual/failed-search")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> searchFailedIndividual(@Valid @RequestBody SearchIndividualDto dto,
                                                    @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.adminIndividualFailedSearch(dto, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/adminfailedsearch")
    @PreAuthorize("hasRole('Onecard_Admin')")
    public ResponseEntity<?> adminFailedSearch(@Valid @RequestBody SearchBulkFailedRechargeDto dto,
                                               @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                               @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.adminFailedSearch(dto, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/individual/{id}")
    public ResponseEntity<?> getBulkIndividualRequest(@PathVariable("id") String id,
                                                      @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                      @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getBulkIndividualRequests(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/individual/search")
    public ResponseEntity<?> searchIndividual(@Valid @RequestBody SearchIndividualDto dto,
                                              @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                              @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.searchIndividual(dto, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> generateExcelFile(@PathVariable("id") String id) {
        final String filename = String.format("%s.%s", id, "xlsx");

        InputStreamResource file = new InputStreamResource(excelGenerator.generateBulkExcelFile(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(Constants.EXCEL_CONTENT_TYPE))
                .body(file);
    }

    @GetMapping("/download-failed")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<Resource> failed(@RequestParam(value = "type") String type) {
        final String filename = String.format("%s.%s", UUID.randomUUID(), "xlsx");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(Constants.EXCEL_CONTENT_TYPE))
                .body(newBulkRechargeService.failed(type));
    }

    @GetMapping("/download-failed-individual")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<Resource> failedIndividual(@RequestParam(value = "id") String id,
                                                     @RequestParam(value = "type") String type) {
        final String filename = String.format("%s.%s", UUID.randomUUID(), "xlsx");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(Constants.EXCEL_CONTENT_TYPE))
                .body(newBulkRechargeService.failedIndividual(id, type));
    }

    @GetMapping("/download-user-bulk/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<Resource> downloadUserBulk(@PathVariable("id") String id) {
        final String filename = String.format("%s.%s", id, "xlsx");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(Constants.EXCEL_CONTENT_TYPE))
                .body(newBulkRechargeService.downloadUserBulk(id));
    }

    @GetMapping("/download-user-individual/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<Resource> downloadUserBulkIndividual(@PathVariable("id") String id) {
        final String filename = String.format("%s.%s", id, "xlsx");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(Constants.EXCEL_CONTENT_TYPE))
                .body(newBulkRechargeService.downloadUserIndividual(id));
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
    public ResponseEntity<?> resolveFailedRequest(@PathVariable("id") Integer id, @Valid @RequestBody ResolveRechargeDto dto) {
        return new ResponseEntity<>(newBulkRechargeService.resolveRecharge(id, dto), HttpStatus.OK);
    }

    @GetMapping("/failed")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getFailedRecharges(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getFailedRequests(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/failedunresolved")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getFailedUnresolvedRecharges(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getFailedUnresolvedRequests(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/failed/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getFailedIndividuals(@PathVariable("id") String id,
                                                  @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getFailedIndividuals(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/failedunresolved/{id}")
    @PreAuthorize("hasRole('ROLE_Onecard_Admin')")
    public ResponseEntity<?> getFailedUnresolvedIndividuals(@PathVariable("id") String id,
                                                            @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(newBulkRechargeService.getFailedUnresolvedIndividuals(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/download")
    public ResponseEntity<Resource> generateDateRangeRecharge(@Valid @RequestBody DateRangeDto dto) {
        final String filename = String.format("%s.%s", UUID.randomUUID(), "xls");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(Constants.EXCEL_CONTENT_TYPE))
                .body(newBulkRechargeService.getRechargeByDateRange(dto));
    }

    @GetMapping("/status/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<RechargeRequestStatusDto> getRechargeRequestStatus(@PathVariable("id") String id) {
        return newBulkRechargeService.getRechargeStatus(id);
    }
}
