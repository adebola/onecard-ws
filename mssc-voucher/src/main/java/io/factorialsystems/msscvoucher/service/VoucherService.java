package io.factorialsystems.msscvoucher.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscvoucher.dao.VoucherMapper;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.dto.in.VoucherChangeRequest;
import io.factorialsystems.msscvoucher.dto.internal.VoucherChangeRequestInternal;
import io.factorialsystems.msscvoucher.web.mapper.VoucherChangeRequestMapper;
import io.factorialsystems.msscvoucher.web.mapper.VoucherMapstructMapper;
import io.factorialsystems.msscvoucher.web.model.PagedDto;
import io.factorialsystems.msscvoucher.web.model.VoucherDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherMapper mapper;
    private final VoucherMapstructMapper voucherMapstructMapper;
    private final VoucherChangeRequestMapper voucherChangeRequestMapper;

    public PagedDto<VoucherDto> findAllVouchers( Integer pageNumber, Integer pageSize) {

        PageHelper.startPage(pageNumber, pageSize);
        return createDto(mapper.findAllVouchers());
    }

    public PagedDto<VoucherDto>searchVouchers(Integer pageNumber, Integer pageSize, String searchString) {

        PageHelper.startPage(pageNumber, pageSize);
        return createDto(mapper.Search(searchString));
    }

    public VoucherDto findVoucherById(Integer id) {
        return voucherMapstructMapper.voucherToVoucherDto(mapper.findVoucherById(id));
    }

    public VoucherDto findVoucherBySerialNumber(String serial) {
        return voucherMapstructMapper.voucherToVoucherDto(mapper.findVoucherBySerialNumber(serial));
    }

    public String deleteVoucher(Integer id) {

        if (mapper.checkVoucherExistsAndNotUsed(id) > 0) {
            final String info = String.format("Voucher %d has been deleted successfully", id);
            mapper.deleteVoucher(id);

            log.info(info);
            return info;
        }

        final String s = String.format("Unable to delete Voucher %d, please make sure it has either been used or does not exist", id);
        log.info(s);
        return s;
    }

    public String changeVoucherDenomination(Integer id, VoucherChangeRequest request) {

        if (mapper.checkVoucherExistsAndNotUsed(id) > 0) {

            Map<String, Object> args = new HashMap<>();

            args.put("id", id);
            args.put("denomination", request.getDenomination());

            mapper.changeVoucherDenomination(args);

            final String infoString = String.format("Voucher: %d had been successfully denominated to %.2f", id, request.getDenomination());
            log.info(infoString);

            return infoString;
        }

        final String s = String.format("Unable to Re-denominate Voucher %d it has either been used or does not exist", id);
        log.info(s);
        return s;
    }

    public String changeVoucherExpiry(Integer id, VoucherChangeRequest request) {
        if (mapper.checkVoucherExistsAndNotUsed(id) > 0) {

            VoucherChangeRequestInternal internal = voucherChangeRequestMapper.toInternal(request);
            Map<String, Object> args = new HashMap<>();

            args.put("id", id);
            args.put("date", internal.getExpiryDate());

            mapper.changeVoucherExpiry(args);

            final String infoString = String.format("Voucher Expiry Date for : %d had been successfully changed to %s", id, internal.getExpiryDate().toString());
            log.info(infoString);

            return infoString;
        }

        final String s = String.format("Unable to change ExpiryDate for Voucher %d it has either been used or does not exist", id);
        log.info(s);
        return s;
    }

    public String activateVoucher(Integer id) {

        if (mapper.checkVoucherExistsAndNotUsed(id) > 0) {

            mapper.activateVoucher(id);

            final String infoString = String.format("Voucher: %d had been successfully activated", id);
            log.info(infoString);

            return infoString;
        }

        final String s = String.format("Unable to Activate Voucher %d it has either been used or does not exist", id);
        log.info(s);
        return s;
    }

    public String deActivateVoucher(Integer id) {

        if (mapper.checkVoucherExistsAndNotUsed(id) > 0) {

            mapper.deActivateVoucher(id);

            final String infoString = String.format("Voucher: %d had been successfully De-activated", id);
            log.info(infoString);

            return infoString;
        }

        final String s = String.format("Unable to De-Activate Voucher %d it has either been used or does not exist", id);
        log.info(s);
        return s;
    }

    public Boolean updateVoucher(Integer id, VoucherDto dto) {

        Voucher voucher = voucherMapstructMapper.voucherDtoToVoucher(dto);
        voucher.setId(id);
        mapper.updateVoucher(voucher);

        return true;
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
