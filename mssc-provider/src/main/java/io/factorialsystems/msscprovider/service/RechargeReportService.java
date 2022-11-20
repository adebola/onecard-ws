package io.factorialsystems.msscprovider.service;

import io.factorialsystems.msscprovider.dao.RechargeReportMapper;
import io.factorialsystems.msscprovider.domain.report.ProviderExpenditure;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RechargeReportService {
    private final RechargeReportMapper rechargeReportMapper;

    public ProviderExpenditure getShortRechargeExpenditure(RechargeProviderRequestDto dto) {
        return rechargeReportMapper.findRechargeProviderExpenditure(dto);
    }
}
