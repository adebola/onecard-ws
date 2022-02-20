package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dto.BulkRechargeRequestDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
@CommonsLog
class BulkRechargeServiceTestService {

    @Autowired
    private BulkRechargeService service;

    @Test
    void saveService() {
        BulkRechargeRequestDto dto = new BulkRechargeRequestDto();
        dto.setServiceCode("MTN-AIRTIME");
        dto.setRecipients(new String[]{"08055572307", "09055572307"});
        dto.setServiceCost(new BigDecimal(100));
        dto.setPaymentMode("wallet");

//        service.saveService(dto);
    }

    @Test
    void runBulkRecharge() {
    }
}

//    @NotNull(message = "Code must be specified")
//    private String serviceCode;
//
//    private Integer groupId;
//    private String[] recipients;
//
//    private String productId;
//    private String redirectUrl;
//    private BigDecimal serviceCost;
//    private String paymentId;
//
//    @Null(message = "Authorization URL cannot be set")
//    private String authorizationUrl;
//
//    private String paymentMode;