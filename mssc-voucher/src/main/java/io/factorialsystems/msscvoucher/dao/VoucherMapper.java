package io.factorialsystems.msscvoucher.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.utils.VoucherGenerationDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface VoucherMapper {
    Page<Voucher> findAllVouchers();
    Voucher findVoucherById(Integer id);
    Page<Voucher> findVouchersByBatchId(String id);
    void generateVouchersForBatch(List<VoucherGenerationDetails> vgds);
    Integer checkVoucherExistsAndNotUsed(Integer id);
    void deleteVoucher(Integer id);
    void changeVoucherDenomination(Map<String, Object> args);
    void changeVoucherExpiry(Map<String, Object> args);
    void activateVoucher(Integer id);
    void deActivateVoucher(Integer id);
}
