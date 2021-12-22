package io.factorialsystems.msscvoucher.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscvoucher.dao.BatchMapper;
import io.factorialsystems.msscvoucher.dao.ClusterMapper;
import io.factorialsystems.msscvoucher.dao.VoucherMapper;
import io.factorialsystems.msscvoucher.domain.Batch;
import io.factorialsystems.msscvoucher.domain.Cluster;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.helper.ExcelHelper;
import io.factorialsystems.msscvoucher.utils.K;
import io.factorialsystems.msscvoucher.utils.VoucherGenerationDetails;
import io.factorialsystems.msscvoucher.web.mapper.BatchMapstructMapper;
import io.factorialsystems.msscvoucher.web.model.BatchDto;
import io.factorialsystems.msscvoucher.web.model.PagedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.Math.abs;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {
    private final BatchMapper batchMapper;
    private final AuditService auditService;
    private final VoucherMapper voucherMapper;
    private final ClusterMapper clusterMapper;
    private final BatchMapstructMapper batchMapstructMapper;

    public static final String BATCH_CREATED = "Batch Created";
    public static final String BATCH_VOUCHER_GENERATED = "Batch Vouchers Generated";
    public static final String BATCH_SUSPENDED = "Batch Suspended";
    public static final String BATCH_UNSUSPENDED = "Batch Un-Suspended";
    public static final String BATCH_ACTIVATED = "Batch Activated";
    public static final String BATCH_UPDATED = "Batch Updated";

    public PagedDto<BatchDto> getAllBatches(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Batch> batches = batchMapper.findAll();

        return createDto(batches);
    }

    public PagedDto<BatchDto> searchBatches(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Batch> batches = batchMapper.search(searchString);

        return createDto(batches);
    }

    public BatchDto getBatch(String id) {
        Batch batch = batchMapper.findById(id);
        return batchMapstructMapper.batchToBatchDto(batch);
    }

    public PagedDto<BatchDto> getBatchByClusterId(Integer pageNumber, Integer pageSize, String id) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Batch> batches = batchMapper.findByClusterId(id);

        return createDto(batches);
    }

    @Transactional
    public BatchDto generateBatch(String userName, BatchDto batchDto) {

        Cluster cluster = clusterMapper.findById(batchDto.getClusterId());

        if (cluster != null) {
            double amount = batchDto.getDenomination().doubleValue() * batchDto.getVoucherCount();

            if (amount < cluster.getBalance().doubleValue()) {
                BigDecimal newBalance = new BigDecimal(cluster.getBalance().doubleValue() - amount);
                cluster.setBalance(newBalance);
                clusterMapper.update(cluster);

                Date expiryDate = batchDto.getExpiryDate();
                String batchId = UUID.randomUUID().toString();
                batchDto.setId(batchId);

                batchDto.setCreatedBy(userName);
                batchMapper.generateBatch(batchMapstructMapper.batchDtoToBatch(batchDto));

                List<VoucherGenerationDetails> voucherGenerationDetails = new ArrayList<>();

                for (int i = 0; i < batchDto.getVoucherCount(); i++) {
                    String encodedString = Base64.getEncoder().encodeToString(String.valueOf(K.generateRandomNumber(16)).getBytes(StandardCharsets.UTF_8));

                    voucherGenerationDetails.add(
                            VoucherGenerationDetails.builder()
                                    .batchId(batchId)
                                    .expiryDate(expiryDate)
                                    .hashedCode(encodedString)
                                    .serialNumber(String.valueOf(K.generateRandomNumber(16)))
                                    .denomination(batchDto.getDenomination())
                                    .build()
                    );
                }

                voucherMapper.generateVouchersForBatch(voucherGenerationDetails);

                String message = String.format("Generated %d Vouchers of %.2f denomination", batchDto.getVoucherCount(), batchDto.getDenomination());
                auditService.auditEvent(message, BATCH_CREATED);

                return batchMapstructMapper.batchToBatchDto(batchMapper.findById(batchId));
            } else {
                throw new RuntimeException("Unable to Generate VoucherBatch total amount exceeds balance on cluster");
            }
        }

        return null;
    }

    public ByteArrayInputStream generateExcelFile(String id) {
        List<Voucher> vouchers = voucherMapper.findVouchersByBatchId(id);

        String message = String.format("Voucher Excel file downloaded for batch %s", id);
        auditService.auditEvent(message, BATCH_VOUCHER_GENERATED);

        return ExcelHelper.vouchersToExcel(vouchers);
    }

    @Transactional
    public BatchDto update(String id, BatchDto batch) {

        // Find Batch in Database
        Batch existingBatch = batchMapper.findById(id);

        if (existingBatch == null) {
            return null;
        }

        //Check if there are date changes
        if (!batch.getExpiryDate().equals(existingBatch.getExpiryDate())) {
            if (!dateCheck(batch.getExpiryDate())) {
                throw new RuntimeException("Invalid Date in Batch Update");
            }

            // Change Expiry of Corresponding Vouchers
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("expiryDate", batch.getExpiryDate());

            batchMapper.changeVoucherExpiry(map);
        }

        // Check if there are Denomination changes
        if (abs(existingBatch.getDenomination().doubleValue() - batch.getDenomination().doubleValue()) > K.epsilon) {
            if (!denominationCheck(batch, existingBatch)) {
                throw new RuntimeException("Invalid Denomination");
            }

            int voucherCount = existingBatch.getVoucherCount();
            double adjustedBalance =
                    (existingBatch.getDenomination().doubleValue() * voucherCount) -
                            (batch.getDenomination().doubleValue() * voucherCount);


            Map<String, Object> map = new HashMap<>();

            map.put("id", id);
            map.put("balance", adjustedBalance);
            map.put("denomination", batch.getDenomination().doubleValue());

            batchMapper.adjustBalances(map);
        }

        // Update the Batch
        batch.setId(id);
        batchMapper.update(batchMapstructMapper.batchDtoToBatch(batch));

        // Audit the Event
        String message = String.format("Batch Updated Excel file downloaded for batch %s", id);
        auditService.auditEvent(message, BATCH_UPDATED);

        // Return the Newly updated Batch
        return batchMapstructMapper.batchToBatchDto(batchMapper.findById(id));
    }

    public BatchDto activateVoucherBatch(String id) {
        Batch batch = batchMapper.findById(id);

        if (batch != null && !batch.getActivated()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("activatedBy", K.getUserName());

            batchMapper.activate(map);

            final String info = String.format("Batch %s activated successfully", id);

            log.info(info);
            auditService.auditEvent(info, BATCH_ACTIVATED);

            return batchMapstructMapper.batchToBatchDto(batchMapper.findById(id));
        }

        return null;
    }

    public BatchDto suspend(String id) {
        Batch batch = batchMapper.findById(id);

        if (batch != null) {
            batchMapper.suspend(id);
            batch.setSuspended(true);

            final String message = String.format("Batch %s Suspended", id);
            auditService.auditEvent(message, BATCH_SUSPENDED);

            return batchMapstructMapper.batchToBatchDto(batch);
        }

        return null;
    }

    public BatchDto unsuspend(String id) {
        Batch batch = batchMapper.findById(id);

        if (batch != null) {
            batchMapper.unsuspend(id);
            batch.setSuspended(false);

            final String message = String.format("Batch %s Un-Suspended", id);
            auditService.auditEvent(message, BATCH_UNSUSPENDED);

            return batchMapstructMapper.batchToBatchDto(batch);
        }

        return null;
    }

    private boolean dateCheck(Date date) {
        return date != null && date.getTime() > new Date().getTime();
    }

    private boolean denominationCheck(BatchDto newBatchDto, Batch oldBatch) {
        Cluster cluster = clusterMapper.findById(newBatchDto.getClusterId());

        if (cluster == null) {
            return false;
        }
        int voucherCount = oldBatch.getVoucherCount();

        double originalBatchAmount = oldBatch.getDenomination().doubleValue() * voucherCount;
        double newBatchAmount = newBatchDto.getDenomination().doubleValue() * voucherCount;

        if (newBatchAmount < originalBatchAmount) {
            return true;
        }

        return !(newBatchAmount > cluster.getBalance().doubleValue());
    }

    private PagedDto<BatchDto> createDto(Page<Batch> batches) {

        PagedDto<BatchDto> pagedDto = new PagedDto<>();
        pagedDto.setPages(batches.getPages());
        pagedDto.setTotalSize((int) batches.getTotal());
        pagedDto.setPageNumber(batches.getPageNum());
        pagedDto.setPageSize(batches.getPageSize());
        pagedDto.setList(batchMapstructMapper.listBatchToBatchDto(batches.getResult()));

        return pagedDto;
    }
}
