package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewScheduledRechargeRequest;
import io.factorialsystems.msscprovider.dto.NewScheduledRechargeRequestDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
@DecoratedWith(NewScheduledRechargeMapstructMapperDecorator.class)
public interface NewScheduledRechargeMapstructMapper {
    @Mappings({
            @Mapping(source = "recipients", target = "recipients"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(target = "scheduledDate", ignore = true),
            @Mapping(target = "requestType",ignore = true),
            @Mapping(target = "paymentMode", ignore = true)
    })
    NewScheduledRechargeRequest rechargeDtoToRecharge(NewScheduledRechargeRequestDto dto);

    @Mappings({
            @Mapping(source = "recipients", target = "recipients"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "paymentMode", target = "paymentMode"),
    })
    NewBulkRechargeRequest ToBulkRechargeRequest(NewScheduledRechargeRequest request);
}
