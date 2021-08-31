package io.factorialsystems.msscvoucher.service;

import com.github.pagehelper.Page;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.dto.in.VoucherChangeRequest;
import io.factorialsystems.msscvoucher.web.model.BatchDto;
import io.factorialsystems.msscvoucher.web.model.PagedDto;
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
        Page<Voucher> vouchers = batchService.getBatchVouchers("92089246-f905-11eb-b55b-8015ea56f0af", 1, 2);
        assertEquals(vouchers.getTotal(), 5);
        assertEquals(vouchers.getPages(), 3);
        assertEquals(vouchers.get(0).getBatchId(), "92089246-f905-11eb-b55b-8015ea56f0af");
    }

    @Test
    public void testNoGetBatchVouchers() {
        Page<Voucher> vouchers = batchService.getBatchVouchers("wrong-string", 1, 2);
        assertEquals(vouchers.getTotal(), 0);
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
        Page<Voucher> vouchers = batchService.getBatchVouchers(batchId, 1, 2);
        assertEquals(vouchers.getTotal(), count);
        assertEquals(vouchers.get(0).getBatchId(), batchId);
    }

    @Test
    void deleteVoucher() {
        String message = batchService.deleteVoucherBatch("92089246-f905-11eb-b55b-8015ea56f0af");
        assertEquals(message, "Batch: 92089246-f905-11eb-b55b-8015ea56f0af has been deleted successfully");
    }

    @Test
    void changeVoucherBatchDenomination() {
        String message = batchService.changeVoucherBatchDenomination("92089246-f905-11eb-b55b-8015ea56f0af", 5000.0);
        assertEquals(message, "Batch: 92089246-f905-11eb-b55b-8015ea56f0af has has successfully changed it denomination to 5000.00");
    }

    @Test
    void changeVoucherBatchExpiry() {
        OffsetDateTime now = OffsetDateTime.now();
        VoucherChangeRequest vcr = new VoucherChangeRequest( 1000.0, now);
        String message = batchService.changeVoucherBatchExpiry("92089246-f905-11eb-b55b-8015ea56f0af", vcr);
        assert(StringUtils.contains(message, "Batch 92089246-f905-11eb-b55b-8015ea56f0af has successfully changed Expiry Date to"));
    }

    @Test
    void activateVoucherBatch() {
        String message = batchService.activateVoucherBatch("92089246-f905-11eb-b55b-8015ea56f0af");
        assertEquals(message, "Batch 92089246-f905-11eb-b55b-8015ea56f0af activated successfully");
    }

    @Test
    void DeActivateVoucherBatch() {
        String message = batchService.deActivateVoucherBatch("92089246-f905-11eb-b55b-8015ea56f0af");
        assertEquals(message, "Batch 92089246-f905-11eb-b55b-8015ea56f0af De-Activated successfully");
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
