package io.factorialsystems.msscprovider.dao;

import io.factorialsystems.msscprovider.dto.RechargeProviderExpenditure;
import io.factorialsystems.msscprovider.dto.report.RechargeProviderRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RechargeReportMapper {
    List<RechargeProviderExpenditure> findRechargeProviderExpenditure(RechargeProviderRequestDto dto);
    List<RechargeProviderExpenditure> findRechargeProviderExpenditurePerDay(RechargeProviderRequestDto dto);
}
