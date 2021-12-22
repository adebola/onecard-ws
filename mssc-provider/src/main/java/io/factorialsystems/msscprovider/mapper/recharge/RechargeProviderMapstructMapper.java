package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.RechargeProvider;
import io.factorialsystems.msscprovider.dto.RechargeProviderDto;
import io.factorialsystems.msscprovider.mapper.DateMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {DateMapper.class})
public interface RechargeProviderMapstructMapper {
    RechargeProviderDto rechargeToRechargeDto(RechargeProvider provider);
    RechargeProvider rechargeDtoToRecharge(RechargeProviderDto dto);
    List<RechargeProviderDto> listRechargeToRechargeDto(List<RechargeProvider> providers);
}
