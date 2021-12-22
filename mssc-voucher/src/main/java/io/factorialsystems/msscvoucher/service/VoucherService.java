package io.factorialsystems.msscvoucher.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscvoucher.dao.VoucherMapper;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.web.mapper.VoucherMapstructMapper;
import io.factorialsystems.msscvoucher.web.model.PagedDto;
import io.factorialsystems.msscvoucher.web.model.VoucherDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherMapper mapper;
    private final VoucherMapstructMapper voucherMapstructMapper;

    public PagedDto<VoucherDto> findAllVouchers( Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(mapper.findAllVouchers());
    }

    public PagedDto<VoucherDto>searchVouchers(Integer pageNumber, Integer pageSize, String searchString) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(mapper.Search(searchString));
    }

    public PagedDto<VoucherDto> getBatchVouchers(String id, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return createDto(mapper.findVouchersByBatchId(id));
    }

    public VoucherDto findVoucherById(Integer id) {
        return voucherMapstructMapper.voucherToVoucherDto(mapper.findVoucherById(id));
    }

    public VoucherDto findVoucherBySerialNumber(String serial) {
        return voucherMapstructMapper.voucherToVoucherDto(mapper.findVoucherBySerialNumber(serial));
    }

    public Boolean updateVoucher(Integer id, VoucherDto dto) {

        Voucher voucher = voucherMapstructMapper.voucherDtoToVoucher(dto);
        voucher.setId(id);
        mapper.updateVoucher(voucher);

        return true;
    }

    public VoucherDto suspendVoucher(Integer id) {
        Voucher voucher = mapper.findVoucherById(id);

        if (voucher != null) {
            voucher.setSuspended(true);
            mapper.suspend(id);

            return voucherMapstructMapper.voucherToVoucherDto(voucher);
        }

        return null;
    }

    public VoucherDto unsuspendVoucher(Integer id) {
        Voucher voucher = mapper.findVoucherById(id);

        if (voucher != null) {
            voucher.setSuspended(false);
            mapper.unsuspend(id);

            return voucherMapstructMapper.voucherToVoucherDto(voucher);
        }

        return null;
    }

    private PagedDto<VoucherDto> createDto(Page<Voucher> vouchers) {
        PagedDto<VoucherDto> pagedDto = new PagedDto<>();
        pagedDto.setPages(vouchers.getPages());
        pagedDto.setTotalSize((int) vouchers.getTotal());
        pagedDto.setPageNumber(vouchers.getPageNum());
        pagedDto.setPageSize(vouchers.getPageSize());
        pagedDto.setList(voucherMapstructMapper.listVoucherToVoucherDto(vouchers.getResult()));

        return pagedDto;
    }
}
