package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.*;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.service.AutoRechargeService;
import io.factorialsystems.msscprovider.service.NewBulkRechargeService;
import io.factorialsystems.msscprovider.service.NewScheduledRechargeService;
import io.factorialsystems.msscprovider.service.SingleRechargeService;
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
@RequestMapping("/api/v1/auth-recharge")
public class RechargeAuthController {
    private final SingleRechargeService rechargeService;
    private final AutoRechargeService autoRechargeService;
    private final NewBulkRechargeService newBulkRechargeService;
    private final NewScheduledRechargeService newScheduledRechargeService;

    @PostMapping
    public ResponseEntity<SingleRechargeResponseDto> startRecharge(@Valid @RequestBody SingleRechargeRequestDto dto) {
        return new ResponseEntity<>(rechargeService.startRecharge(dto), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> finishRecharge(@PathVariable("id") String id) {
        RechargeStatus status = rechargeService.finishRecharge(id);

        if (status == null || status.getMessage() == null) {
            return new ResponseEntity<>(new MessageDto("Recharge Failed"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new MessageDto(status.getMessage()), status.getStatus());
    }

    //
    // Please use /api/v1/auth-recharge/bulk in BulkRechargeAuthController::startNewBulkRecharge
    //
    @Deprecated
    @PostMapping("/newbulk")
    public ResponseEntity<NewBulkRechargeResponseDto> startNewBulkRecharge(@Valid @RequestBody NewBulkRechargeRequestDto dto) {
        return new ResponseEntity<>(newBulkRechargeService.saveService(dto), HttpStatus.OK);
    }

    //
    // Please use /api/v1/auth-recharge/bulk/{id} in BulkRechargeAuthController::finishNewBulkRecharge
    //
    @Deprecated
    @GetMapping("/newbulk/{id}")
    public ResponseEntity<MessageDto> finishNewBulkRecharge(@PathVariable("id") String id) {
        newBulkRechargeService.asyncRecharge(id);
        return new ResponseEntity<>(new MessageDto("Request submitted successfully"), HttpStatus.OK);
    }

    //
    // Please use /api/v1/auth-recharge/auto/list in AutoRechargeAuthController::getUserAutoRecharges
    //
    @Deprecated
    @GetMapping("/getauto")
    public ResponseEntity<?> getUserAutoRecharges() {
        return new ResponseEntity<>(autoRechargeService.findUserRecharges(), HttpStatus.OK);
    }


    //
    // Please use /api/v1/auth-recharge/bulk/file in BulkRechargeAuthController::uploadBulkFile
    //
    @Deprecated
    @PostMapping("/bulkfile")
    public ResponseEntity<?> uploadBulkFile(@RequestPart(value = "file") MultipartFile file) {
        newBulkRechargeService.uploadRecharge(file);
        return new ResponseEntity<>(new MessageDto("Bulk Request has been submitted successfully, results will be mailed to you"), HttpStatus.ACCEPTED);
    }


    //
    // Please use /api/v1/auth-recharge/scheduled/file in ScheduledRechargeAuthController::uploadScheduleFile
    //
    @Deprecated
    @PostMapping("/schedulefile")
    public ResponseEntity<?> uploadScheduleFile(@RequestPart(value = "date") DateDto dto, @RequestPart(value = "file") MultipartFile file) {
        newScheduledRechargeService.uploadRecharge(file, dto.getScheduledDate());
        return new ResponseEntity<>(new MessageDto("Scheduled Request has been submitted successfully, results will be mailed to you"), HttpStatus.ACCEPTED);
    }


    //
    // Please use /api/v1/auth-recharge/auto/file in AutoRechargeAuthController::uploadAutoFile
    //
    @Deprecated
    @PostMapping("/autofile")
    public ResponseEntity<?> uploadAutoFile(@RequestPart(value = "auto") AutoUploadFileRechargeRequestDto dto, @RequestPart(value = "file") MultipartFile file) {
        return new ResponseEntity<>(autoRechargeService.uploadRecharge(dto, file), HttpStatus.ACCEPTED);
    }

    //
    // Please use /api/v1/auth-recharge/auto/list in AutoRechargeAuthController::getUserBulkRecharges
    //
    @Deprecated
    @GetMapping("/bulklist")
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

    @GetMapping("/singlelist")
    public ResponseEntity<?> getUserSingleRecharges(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                    @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = K.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = K.DEFAULT_PAGE_SIZE;
        }
        return new ResponseEntity<>(rechargeService.getUserRecharges(pageNumber, pageSize), HttpStatus.OK);
    }

    @Deprecated
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

    //
    // Please use /api/v1/auth-recharge/scheduled/list in ScheduledRechargeAuthController::getUserScheduleRecharges
    //
    @Deprecated
    @GetMapping("/schedulelist")
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

    //
    // Please use /api/v1/auth-recharge/scheduled/individual/{id} in ScheduledRechargeAuthController::getScheduleBulkIndividualRequest
    //
    @Deprecated
    @GetMapping("/schedule/{id}")
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