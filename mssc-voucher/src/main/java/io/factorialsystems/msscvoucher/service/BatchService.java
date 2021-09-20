package io.factorialsystems.msscvoucher.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscvoucher.dao.BatchMapper;
import io.factorialsystems.msscvoucher.dao.VoucherMapper;
import io.factorialsystems.msscvoucher.domain.Batch;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.dto.in.VoucherChangeRequest;
import io.factorialsystems.msscvoucher.dto.internal.VoucherChangeRequestInternal;
import io.factorialsystems.msscvoucher.helper.ExcelHelper;
import io.factorialsystems.msscvoucher.utils.K;
import io.factorialsystems.msscvoucher.utils.VoucherGenerationDetails;
import io.factorialsystems.msscvoucher.web.mapper.BatchMapstructMapper;
import io.factorialsystems.msscvoucher.web.mapper.VoucherChangeRequestMapper;
import io.factorialsystems.msscvoucher.web.mapper.VoucherMapstructMapper;
import io.factorialsystems.msscvoucher.web.model.BatchDto;
import io.factorialsystems.msscvoucher.web.model.PagedDto;
import io.factorialsystems.msscvoucher.web.model.VoucherDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchMapper batchMapper;
    private final VoucherMapper voucherMapper;
    private final VoucherChangeRequestMapper requestMapper;
    private final BatchMapstructMapper batchMapstructMapper;
    private final VoucherMapstructMapper voucherMapstructMapper;

    public PagedDto<BatchDto> getAllBatches(Integer pageNumber, Integer pageSize) {

        PageHelper.startPage(pageNumber, pageSize);
        Page<Batch> batches = batchMapper.findAllBatches();

        return createDto(batches);
    }

    public PagedDto<BatchDto>searchBatches(Integer pageNumber, Integer pageSize, String searchString) {

        PageHelper.startPage(pageNumber, pageSize);
        Page<Batch> batches = batchMapper.Search(searchString);

        return createDto(batches);
    }

    public BatchDto getBatch(String id) {
        Batch batch = batchMapper.findBatch(id);
        return batchMapstructMapper.batchToBatchDto(batch);
    }

    public PagedDto<VoucherDto> getBatchVouchers(String id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createVoucherDto(voucherMapper.findVouchersByBatchId(id));
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

    private PagedDto<VoucherDto> createVoucherDto(Page<Voucher> vouchers) {
        PagedDto<VoucherDto> pagedDto = new PagedDto<>();
        pagedDto.setPages(vouchers.getPages());
        pagedDto.setTotalSize((int) vouchers.getTotal());
        pagedDto.setPageNumber(vouchers.getPageNum());
        pagedDto.setPageSize(vouchers.getPageSize());
        pagedDto.setList(voucherMapstructMapper.listVoucherToVoucherDto(vouchers.getResult()));

        return pagedDto;
    }

    public BatchDto generateBatch(String userName, BatchDto batchDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Date expiryDate = batchDto.getExpiryDate();

        String batchId = UUID.randomUUID().toString();
        Map<String, Object> args = new HashMap<>();

        args.put("id", batchId);
        args.put("createdBy", userName);
        args.put("amount", batchDto.getDenomination());
        args.put("count", batchDto.getCount());
        args.put("date", expiryDate);

        batchMapper.generateBatch(args);

        List<VoucherGenerationDetails> voucherGenerationDetails = new ArrayList<>();

        for (int i = 0; i < batchDto.getCount(); i++) {
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

        return batchMapstructMapper.batchToBatchDto(batchMapper.findBatch(batchId));
    }

    public ByteArrayInputStream generateExcelFile(String id) {
        List<Voucher> vouchers = voucherMapper.findVouchersByBatchId(id);
        return ExcelHelper.vouchersToExcel(vouchers);
    }

    public BatchDto updateBatch(String id, BatchDto batchDto) {
        return null;
    }


    public String deleteVoucherBatch(String id) {

         if (batchMapper.checkBatchExists(id) != 1) {
             final String info = "The Batch you have chosen to delete is nonexistent: " + id;
             log.info(info);
             return info;
         }

        if (batchMapper.checkBatchUsed(id) > 0) {
            final String info = String.format("The Batch %s has Vouchers that have already been used cannot be deleted", id);
            log.info(info);
            return info;
        }

        batchMapper.deleteBatch(id);
        final String info = String.format("Batch: %s has been deleted successfully", id);
        log.info(info);

        return  info;
    }

    public String changeVoucherBatchDenomination(String id, Double denomination) {

        if (batchMapper.checkBatchExists(id) != 1) {
            final String info = "The Batch you have chosen to change its denomination is nonexistent: " + id;
            log.info(info);
            return info;
        }

        if (batchMapper.checkBatchUsed(id) > 0) {
            final String info = String.format("The Batch %s has Vouchers that have already been used denomination cannot be changed", id);
            log.info(info);
            return info;
        }

        Map<String, Object> args = new HashMap<>();

        args.put("id", id);
        args.put("denomination", denomination);

        batchMapper.changeDenomination(args);

        final String info = String.format("Batch: %s has has successfully changed it denomination to %.2f", id, denomination);
        log.info(info);

        return  info;
    }

    public String changeVoucherBatchExpiry(String id, VoucherChangeRequest request) {
        if (batchMapper.checkBatchExists(id) != 1) {
            final String info = "The Batch you have chosen to change its expiry date is nonexistent: " + id;
            log.info(info);
            return info;
        }

        if (batchMapper.checkBatchUsed(id) > 0) {
            final String info = String.format("The Batch %s has Vouchers that have already been used expiry date cannot be changed", id);
            log.info(info);
            return info;
        }

        VoucherChangeRequestInternal internal = requestMapper.toInternal(request);
        Map<String, Object> args = new HashMap<>();

        args.put("id", id);
        args.put("date", internal.getExpiryDate());

        batchMapper.changeExpiry(args);

        final String info = String.format("Batch %s has successfully changed Expiry Date to %s", id, internal.getExpiryDate().toString());
        log.info(info);
        return info;
    }

    public String activateVoucherBatch(String id) {
        batchMapper.activateBatch(id);

        final String info = String.format("Batch %s activated successfully", id);
        log.info(info);
        return info;

    }

    public String deActivateVoucherBatch(String id) {
        batchMapper.deActivateBatch(id);

        final String info = String.format("Batch %s De-Activated successfully", id);
        log.info(info);
        return info;
    }
}
