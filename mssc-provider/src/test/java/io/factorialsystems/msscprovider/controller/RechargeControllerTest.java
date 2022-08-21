package io.factorialsystems.msscprovider.controller;

import io.factorialsystems.msscprovider.dto.recharge.SingleRechargeRequestDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

@CommonsLog
class RechargeControllerTest {

    @Test
    void startRecharge() {
        SingleRechargeRequestDto dto = new SingleRechargeRequestDto();
        dto.setId(UUID.randomUUID().toString());
        dto.setCreatedAt(new Date());
    }

    @Test
    void finishRecharge() {
    }

    @Test
    void getDataPlans() {
    }

    @Test
    void getExtraDataPlans() {
    }
}