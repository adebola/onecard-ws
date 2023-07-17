package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.dto.RechargeProviderExpenditure;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RechargeReportMapperTest {

    @Autowired
    RechargeReportMapper rechargeReportMapper;

    @Test
    void findRechargeProviderExpenditure() {
        RechargeProviderRequestDto dto = new RechargeProviderRequestDto();
        final List<RechargeProviderExpenditure> results = rechargeReportMapper.findRechargeProviderExpenditure(dto);
        assertThat(results).isNotNull();
        assertThat(results.size()).isGreaterThan(0);

        log.info("Results SIZE {}", results.size());
        log.info("Results {}", results);
    }

    @Test
    void findRechargeProviderExpenditurePerDay() {
        RechargeProviderRequestDto dto = new RechargeProviderRequestDto();
        final List<RechargeProviderExpenditure> results = rechargeReportMapper.findRechargeProviderExpenditurePerDay(dto);
        assertThat(results).isNotNull();
        assertThat(results.size()).isGreaterThan(0);

        log.info("Results SIZE {}", results.size());
        log.info("Results {}", results);
    }
}