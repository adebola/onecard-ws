package io.factorialsystems.msscvoucher.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.utils.VoucherGenerationDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VoucherMapper {
    Page<Voucher> findAllVouchers();
    Voucher findVoucherById(Integer id);
    Voucher findVoucherBySerialNumber(String serial);
    Page<Voucher> findVouchersByBatchId(String id);
    Page<Voucher> Search(String search);
    void generateVouchersForBatch(List<VoucherGenerationDetails> vgds);
    void updateVoucher(Voucher voucher);
    void suspend(Integer id);
    void unsuspend(Integer id);
}
