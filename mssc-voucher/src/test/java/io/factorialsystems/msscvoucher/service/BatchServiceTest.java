package io.factorialsystems.msscvoucher.service;

import io.factorialsystems.msscvoucher.dto.in.VoucherChangeRequest;
import io.factorialsystems.msscvoucher.web.model.BatchDto;
import io.factorialsystems.msscvoucher.web.model.PagedDto;
import io.factorialsystems.msscvoucher.web.model.VoucherDto;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CommonsLog
@SpringBootTest
class BatchServiceTest {

    @Autowired
    BatchService batchService;

    @Test
    public void testGetAllBatches() {
        PagedDto<BatchDto> dto = batchService.getAllBatches(1,2);
        log.info(dto);
        log.info(batchService.getAllBatches(5,2));


//        Page<Batch> batches = batchService.getAllBatches(1,2);
//        assert(batches.getTotal() > 0);
//        log.info(batches.get(0));
    }

    @Test
    public void testGetBatchVouchers() {
        PagedDto<VoucherDto> vouchers = batchService.getBatchVouchers("244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b", 1, 2);
//        assertEquals(vouchers.getTotal(), 5);
//        assertEquals(vouchers.getPages(), 3);
        assertEquals(vouchers.getList().get(0).getBatchId(), "244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b");
    }

    @Test
    public void testNoGetBatchVouchers() {
        PagedDto<VoucherDto> vouchers = batchService.getBatchVouchers("wrong-string", 1, 2);
        assertEquals(vouchers.getTotalSize(), 0);
        assertEquals(vouchers.getPages(), 0);
    }

    @Test
    public void generateVoucherBatchTest() {
        int count = 2;
        String batchId;
        BatchDto dto = BatchDto.builder()
                .count(count)
                .denomination(BigDecimal.valueOf(1000))
                .expiryDate(new Date())
                .createdBy("adebola")
                .build();

        BatchDto batchDto = batchService.generateBatch("adebola", dto);
        batchId = batchDto.getId();
        PagedDto<VoucherDto> vouchers = batchService.getBatchVouchers(batchId, 1, 2);
        assertEquals(vouchers.getTotalSize(), count);
        assertEquals(vouchers.getList().get(0).getBatchId(), batchId);
    }

    @Test
    void deleteVoucher() {
        String message = batchService.deleteVoucherBatch("244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b");
        assertEquals(message, "Batch: 244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b has been deleted successfully");
    }

    @Test
    void changeVoucherBatchDenomination() {
        String message = batchService.changeVoucherBatchDenomination("244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b", 5000.0);
        assertEquals(message, "Batch: 244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b has has successfully changed it denomination to 5000.00");
    }

    @Test
    void changeVoucherBatchExpiry() {
        OffsetDateTime now = OffsetDateTime.now();
        VoucherChangeRequest vcr = new VoucherChangeRequest( 1000.0, now);
        String message = batchService.changeVoucherBatchExpiry("244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b", vcr);
        assert(StringUtils.contains(message, "Batch 244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b has successfully changed Expiry Date to"));
    }

    @Test
    void activateVoucherBatch() {
        String message = batchService.activateVoucherBatch("244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b");
        assertEquals(message, "Batch 244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b activated successfully");
    }

    @Test
    void DeActivateVoucherBatch() {
        String message = batchService.deActivateVoucherBatch("244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b");
        assertEquals(message, "Batch 244bdc2b-0cae-4e01-a3f9-d1b0310aeb0b De-Activated successfully");
    }

    @Test
    void getBatchVouchers() {
    }

    @Test
    void generateBatch() {
    }

    @Test
    void deleteVoucherBatch() {
        String s = batchService.deleteVoucherBatch("d205ee54-3e1c-4bb4-a286-222eaaa9563d");
        log.info(s);
    }
}
