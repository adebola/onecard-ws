package io.factorialsystems.msscvoucher.service;

import io.factorialsystems.msscvoucher.dao.BatchMapper;
import io.factorialsystems.msscvoucher.dao.VoucherMapper;
import io.factorialsystems.msscvoucher.domain.Batch;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.web.model.BatchDto;
import io.factorialsystems.msscvoucher.web.model.PagedDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@CommonsLog
@SpringBootTest
class BatchServiceTest {

    @Autowired
    BatchService batchService;

    @Autowired
    BatchMapper batchMapper;

    @Autowired
    VoucherMapper voucherMapper;

    @Test
    public void testGetAllBatches() {
        PagedDto<BatchDto> dto = batchService.getAllBatches(1, 2);
        log.info(dto);
        log.info(batchService.getAllBatches(5, 2));
        assertNotNull(dto);
        assert (dto.getPageSize() > 0);
    }


    @Test
    public void generateVoucherBatchTest() {
        int count = 5;
        String batchId;
        String id = UUID.randomUUID().toString();
        BatchDto dto = BatchDto.builder()
                .id(id)
                .clusterId("0d7d07b2-43a8-11ec-8b30-35fc519e26e2")
                .voucherCount(count)
                .denomination(BigDecimal.valueOf(1500))
                .createdBy("adebola")
                .build();

        BatchDto batchDto = batchService.generateBatch("adebola", dto);
        batchId = batchDto.getId();

        List<Voucher> vouchers = voucherMapper.findVouchersByBatchId(batchId);
        assertNotNull(vouchers);
        assertEquals(count, vouchers.size());
        vouchers.stream().forEach(voucher -> assertEquals(batchId, voucher.getBatchId()));

        log.info(vouchers);
    }

    @Test
    void activateVoucherBatch() {

        String id = "047e97cb-5cb6-48c0-9db3-24f4410863f2";

        batchService.activateVoucherBatch(id);
        Batch batch = batchMapper.findById(id);

        assertNotNull(batch);
        assertEquals(id, batch.getId());

        List<Voucher> vouchers = voucherMapper.findVouchersByBatchId(id);

        vouchers.stream().forEach(v -> assertEquals(true, v.getActivated()));
        log.info(vouchers);
    }

    @Test
    void update() throws ParseException {
        String id = "047e97cb-5cb6-48c0-9db3-24f4410863f2";
        String dateString = "2030-12-01";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        BigDecimal decimal = new BigDecimal(1255);

        BatchDto batchDto = BatchDto.builder()
                .id(id)
                .clusterId("0d7d07b2-43a8-11ec-8b30-35fc519e26e2")
                .expiryDate(date)
                .denomination(decimal)
                .build();

        BatchDto dto = batchService.update(id, batchDto);
        BatchDto compareDto = batchService.getBatch(id);

        assertNotNull(dto);
        assertNotNull(compareDto);

        assertEquals(dto.getId(), compareDto.getId());
    }

    @Test
    void suspend() {
        String id = "047e97cb-5cb6-48c0-9db3-24f4410863f2";
        batchService.suspend(id);
    }
}
