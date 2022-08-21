package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.RechargeProvider;
import io.factorialsystems.msscprovider.domain.RechargeProviderEx;
import io.factorialsystems.msscprovider.dto.provider.RechargeProviderDto;
import io.factorialsystems.msscprovider.dto.provider.RechargeProviderExDto;
import io.factorialsystems.msscprovider.mapper.DateMapper;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = {DateMapper.class})
public interface RechargeProviderMapstructMapper {
    RechargeProviderDto rechargeToRechargeDto(RechargeProvider provider);
    RechargeProvider rechargeDtoToRecharge(RechargeProviderDto dto);
    List<RechargeProviderDto> listRechargeToRechargeDto(List<RechargeProvider> providers);
    RechargeProviderExDto rechargeProviderExToRechargeProviderExDto(RechargeProviderEx provider);
    List<RechargeProviderExDto> listRechargeProviderExToRechargeProviderExDto(List<RechargeProviderEx> providers);
}
