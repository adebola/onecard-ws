package io.factorialsystems.msscvoucher.service;

import io.factorialsystems.msscvoucher.dao.VoucherMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
//        PagedDto<VoucherDto> vouchers = voucherService.findAllVouchers(1,2);
//         assert(vouchers.getTotalSize() > 0);
//         log.info(vouchers);
    }

    @Test
    public void decodePin() {
//        byte[] decodedBytes = Base64.getDecoder().decode("OTk1NTAyNTMzMjk2MzM1Mg==");
//        String decodedString = new String(decodedBytes);
//        log.info(decodedString);
    }

    @Test
    public void findVoucherById() {
//        VoucherDto voucher = voucherService.findVoucherById(1);
//        assert (voucher != null);
//        log.info(voucher);
    }


    @Test
    public void findVoucherBySerial() {
//        String serial = "8256459556422042";
//        Voucher voucher = mapper.findVoucherBySerialNumber(serial);
//        assertNotNull(voucher);
//        assertEquals(serial, voucher.getSerialNumber());
//        log.info(voucher);
    }

    @Test
    public void updateVoucher() {
//        VoucherDto voucherDto = VoucherDto.builder()
//                .denomination(new BigDecimal(1200.0))
//                .activated(true)
//                .expiryDate(new Date())
//                .build();
//
//        log.info(voucherDto);
//        voucherService.updateVoucher(1, voucherDto);
    }
}
