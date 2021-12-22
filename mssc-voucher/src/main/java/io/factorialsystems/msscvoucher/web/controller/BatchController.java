package io.factorialsystems.msscvoucher.web.controller;

import io.factorialsystems.msscvoucher.dto.out.MessageDto;
import io.factorialsystems.msscvoucher.service.BatchService;
import io.factorialsystems.msscvoucher.utils.K;
import io.factorialsystems.msscvoucher.web.model.BatchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_NUMBER;
import static io.factorialsystems.msscvoucher.utils.K.DEFAULT_PAGE_SIZE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batch")
public class BatchController {
    private final BatchService batchService;

    @GetMapping
    public ResponseEntity<?> getBatches(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                        @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(batchService.getAllBatches(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBatches(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                           @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                           @RequestParam(value = "searchString") String searchString) {

        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(batchService.searchBatches(pageNumber, pageSize, searchString), HttpStatus.OK);
    }

    @GetMapping("/{id}/cluster")
    public ResponseEntity<?> getBatchByClusterId(@PathVariable("id") String id,
                                                 @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        if (pageNumber == null || pageNumber < 0) {
            pageNumber = DEFAULT_PAGE_NUMBER;
        }

        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        return new ResponseEntity<>(batchService.getBatchByClusterId(pageNumber, pageSize, id), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchDto> getBatch(@PathVariable("id") String id) {
        return new ResponseEntity<>(batchService.getBatch(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> generateExcelFile(@PathVariable("id") String id) {
        String filename = "onecard.xlsx";

        InputStreamResource file = new InputStreamResource(batchService.generateExcelFile(id));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);

    }

    @PostMapping
    public ResponseEntity<?> generateBatchVouchers(@Valid @RequestBody BatchDto request) {

        try {
            BatchDto batchDto = batchService.generateBatch(K.getUserName(), request);

            if (batchDto != null) {
                return new ResponseEntity<>(batchDto, HttpStatus.CREATED);
            }

            return new ResponseEntity<>(new MessageDto("Unable to Find Cluster for Batch"), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>(new MessageDto(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBatch(@PathVariable("id") String id, @Valid @RequestBody BatchDto dto) {
        try {
            BatchDto batchDto = batchService.update(id, dto);

            if (batchDto == null) {
                return new ResponseEntity<>(new MessageDto("No Batch found"), HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(batchDto, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new MessageDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/activate")
    public ResponseEntity<?> activateVoucherBatch(@PathVariable("id") String id) {
        try {
            BatchDto batchDto = batchService.activateVoucherBatch(id);

            if (batchDto == null) {
                return new ResponseEntity<>(new MessageDto("No Batch found"), HttpStatus.BAD_REQUEST);
            }

            return new ResponseEntity<>(batchDto, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(new MessageDto(ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/suspend")
    public ResponseEntity<?> suspendBatch(@PathVariable("id") String id) {
        BatchDto dto = batchService.suspend(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto("No Batch found"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping("/{id}/unsuspend")
    public ResponseEntity<?> unsuspendBatch(@PathVariable("id") String id) {
        BatchDto dto = batchService.unsuspend(id);

        if (dto == null) {
            return new ResponseEntity<>(new MessageDto("No Batch found"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
