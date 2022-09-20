package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.recharge.AutoRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.AutoUploadFileRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.DateDto;
import io.factorialsystems.msscprovider.service.AutoRechargeService;
import io.factorialsystems.msscprovider.utils.Constants;
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
@RequestMapping("/api/v1/auth-recharge/auto")
public class AutoRechargeAuthController {
    private final AutoRechargeService autoRechargeService;

    @PostMapping
    public ResponseEntity<?> startAutoRecharge(@Valid @RequestBody AutoRechargeRequestDto dto) {
        return new ResponseEntity<>(autoRechargeService.saveService(dto), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAutoRecharge(@PathVariable("id") String id) {
        return new ResponseEntity<>(autoRechargeService.getSingleService(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAutoRecharge(@PathVariable("id") String id, @Valid @RequestBody AutoRechargeRequestDto dto) {
        autoRechargeService.updateService(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAutoRecharge(@PathVariable("id") String id) {
        autoRechargeService.deleteService(id);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getUserAutoRecharges(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(autoRechargeService.findUserRecharges(pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<?> uploadAutoFile(@RequestPart(value = "auto") AutoUploadFileRechargeRequestDto dto, @RequestPart(value = "file") MultipartFile file) {
        return new ResponseEntity<>(autoRechargeService.uploadRecharge(dto, file), HttpStatus.ACCEPTED);
    }

    @GetMapping("/bulk/{id}")
    public ResponseEntity<?> getAutoBulkRecharges(@PathVariable("id") String id,
                                                  @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                  @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(autoRechargeService.getBulkRecharges(id, pageNumber, pageSize), HttpStatus.OK);
    }

    @PostMapping("/searchdate")
    public ResponseEntity<?> searchByDate(@Valid @RequestBody DateDto dateDto,
                                          @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(autoRechargeService.searchByDate(dateDto.getScheduledDate(), pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/searchname")
    public ResponseEntity<?> searchByName(@RequestParam(value = "name") String name,
                                          @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                          @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = Constants.DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(autoRechargeService.searchByName(name, pageNumber, pageSize), HttpStatus.OK);
    }
}
