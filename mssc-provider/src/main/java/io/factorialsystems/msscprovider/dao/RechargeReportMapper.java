package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.domain.report.ProviderExpenditure;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RechargeReportMapper {
    ProviderExpenditure findRechargeProviderExpenditure(RechargeProviderRequestDto dto);
}
