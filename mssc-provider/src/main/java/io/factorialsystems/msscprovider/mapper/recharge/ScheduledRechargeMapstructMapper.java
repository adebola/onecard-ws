package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.ScheduledRechargeRequest;
import io.factorialsystems.msscprovider.dto.ScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.mapper.DateMapper;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(ScheduledRechargeMapstructMapperDecorator.class)
public interface ScheduledRechargeMapstructMapper {

    @Mappings({
            @Mapping(source = "groupId", target = "groupId"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "recipients", target = "recipients"),
            @Mapping(target = "paymentMode",ignore = true),
            @Mapping(target = "scheduledDate", ignore = true),
            @Mapping(target = "serviceCode", ignore = true),
            @Mapping(target = "authorizationUrl", ignore = true)
    })
    ScheduledRechargeRequest rechargeDtoToRecharge(ScheduledRechargeRequestDto dto);
}
