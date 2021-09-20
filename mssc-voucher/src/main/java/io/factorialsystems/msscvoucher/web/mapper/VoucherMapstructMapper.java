package io.factorialsystems.msscvoucher.web.mapper;

import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.web.model.VoucherDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = DateMapper.class)
public interface VoucherMapstructMapper {

    @Mapping(target = "createdDate", source = "createdAt")
    VoucherDto voucherToVoucherDto(Voucher voucher);
    Voucher voucherDtoToVoucher(VoucherDto voucherDto);
    List<VoucherDto> listVoucherToVoucherDto(List<Voucher> vouchers);
}
