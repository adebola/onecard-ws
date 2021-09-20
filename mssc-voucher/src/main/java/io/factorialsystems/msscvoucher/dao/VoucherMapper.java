package io.factorialsystems.msscvoucher.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscvoucher.domain.Batch;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.utils.VoucherGenerationDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface VoucherMapper {
    Page<Voucher> findAllVouchers();
    Voucher findVoucherById(Integer id);
    Voucher findVoucherBySerialNumber(String serial);
    Page<Voucher> findVouchersByBatchId(String id);
    Page<Voucher> Search(String search);
    void generateVouchersForBatch(List<VoucherGenerationDetails> vgds);
    void updateVoucher(Voucher voucher);
    Integer checkVoucherExistsAndNotUsed(Integer id);
    void deleteVoucher(Integer id);
    void changeVoucherDenomination(Map<String, Object> args);
    void changeVoucherExpiry(Map<String, Object> args);
    void activateVoucher(Integer id);
    void deActivateVoucher(Integer id);
}
