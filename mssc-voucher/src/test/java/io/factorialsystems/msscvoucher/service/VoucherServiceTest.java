package io.factorialsystems.msscvoucher.service;

import com.github.pagehelper.Page;
import io.factorialsystems.msscvoucher.dao.VoucherMapper;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.dto.in.VoucherChangeRequest;
import io.factorialsystems.msscvoucher.web.model.PagedDto;
import io.factorialsystems.msscvoucher.web.model.VoucherDto;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CommonsLog
@SpringBootTest
class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherMapper mapper;

    private String[] months = {
            "JANUARY",
            "FEBRUARY",
            "MARCH",
            "APRIL",
            "MAY",
            "JUNE",
            "JULY",
            "AUGUST",
            "SEPTEMBER",
            "OCTOBER",
            "NOVEMBER",
            "DECEMBER"
    };

    @Test
    public void testFindAllVouchers() {
        PagedDto<VoucherDto> vouchers = voucherService.findAllVouchers(1,2);
         assert(vouchers.getTotalSize() > 0);
         log.info(vouchers);
    }

    @Test
    public void decodePin() {
        byte[] decodedBytes = Base64.getDecoder().decode("OTk1NTAyNTMzMjk2MzM1Mg==");
        String decodedString = new String(decodedBytes);
        log.info(decodedString);
    }

    @Test
    public void findVoucherById() {
        VoucherDto voucher = voucherService.findVoucherById(1);
        assert (voucher != null);
        log.info(voucher);
    }

    @Test
    public void findVoucherBySerialNumber() {
        VoucherDto voucherDto = voucherService.findVoucherBySerialNumber("6592058283228269");
        assertEquals(voucherDto.getSerialNumber(), "6592058283228269");
        log.info(voucherDto);
    }

    @Test
    public void findVoucherBySerialDao() {
        Voucher voucher = mapper.findVoucherBySerialNumber("6592058283228269");
        assert (voucher != null);
        log.info(voucher);
    }

    @Test
    public void changeVoucherDenomination() {
        Double denomination = 2500.0;

        VoucherChangeRequest request = new VoucherChangeRequest(denomination, null);
        voucherService.changeVoucherDenomination(1, request);

        VoucherDto voucher = voucherService.findVoucherById(1);

        assert (voucher != null);
        assert (Math.abs(voucher.getDenomination().floatValue() - denomination) < 0.1);

    }

    @Test
    public void changeVoucherExpiry() {
        OffsetDateTime now = OffsetDateTime.now();
        VoucherChangeRequest request = new VoucherChangeRequest(1000.0, now);
        voucherService.changeVoucherExpiry(1, request);

        VoucherDto voucher = voucherService.findVoucherById(1);

        assert (voucher != null);

        assertEquals (now.getYear(), voucher.getExpiryDate().getYear() + 1900);
        assertEquals(now.getMonth().toString(), months[voucher.getExpiryDate().getMonth()]);
        assertEquals(now.getDayOfMonth(), voucher.getExpiryDate().getDate());
    }

    @Test
    public void activateVoucher() {
        voucherService.activateVoucher(1);
        VoucherDto voucher = voucherService.findVoucherById(1);
        log.info(voucher);
    }

    @Test
    public void deActivateVoucher() {
        voucherService.deActivateVoucher(1);
        VoucherDto voucher = voucherService.findVoucherById(1);
        log.info(voucher);
    }

    @Test
    public void updateVoucher() {
        VoucherDto voucherDto = VoucherDto.builder()
                .denomination(new BigDecimal(1200.0))
                .activated(true)
                .expiryDate(new Date())
                .build();

        log.info(voucherDto);
        voucherService.updateVoucher(1, voucherDto);
    }
}
