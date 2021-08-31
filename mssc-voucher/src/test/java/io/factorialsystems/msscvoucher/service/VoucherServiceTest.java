package io.factorialsystems.msscvoucher.service;

import com.github.pagehelper.Page;
import io.factorialsystems.msscvoucher.domain.Voucher;
import io.factorialsystems.msscvoucher.dto.in.VoucherChangeRequest;
import lombok.extern.apachecommons.CommonsLog;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CommonsLog
@SpringBootTest
class VoucherServiceTest {

    @Autowired
    private VoucherService voucherService;

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
        Page<Voucher> vouchers = voucherService.findAllVouchers(1,2);
       assert(vouchers.getTotal() > 0);
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
        Voucher voucher = voucherService.findVoucherById(1);
        assert (voucher != null);
        assertEquals (voucher.getBatchId(), "92089246-f905-11eb-b55b-8015ea56f0af");
    }

    @Test
    public void changeVoucherDenomination() {
        Double denomination = 2500.0;

        VoucherChangeRequest request = new VoucherChangeRequest(denomination, null);
        voucherService.changeVoucherDenomination(1, request);

        Voucher voucher = voucherService.findVoucherById(1);

        assert (voucher != null);
        assert (Math.abs(voucher.getDenomination().floatValue() - denomination) < 0.1);

    }

    @Test
    public void changeVoucherExpiry() {
        OffsetDateTime now = OffsetDateTime.now();
        VoucherChangeRequest request = new VoucherChangeRequest(1000.0, now);
        voucherService.changeVoucherExpiry(1, request);

        Voucher voucher = voucherService.findVoucherById(1);

        assert (voucher != null);


        assertEquals (now.getYear(), voucher.getExpiryDate().getYear() + 1900);
        assertEquals(now.getMonth().toString(), months[voucher.getExpiryDate().getMonth()]);
        assertEquals(now.getDayOfMonth(), voucher.getExpiryDate().getDate());
    }

    @Test
    public void activateVoucher() {
        voucherService.activateVoucher(1);
        Voucher voucher = voucherService.findVoucherById(1);
        log.info(voucher);
    }

    @Test
    public void deActivateVoucher() {
        voucherService.deActivateVoucher(1);
        Voucher voucher = voucherService.findVoucherById(1);
        log.info(voucher);
    }
}
