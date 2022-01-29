package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.ScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
@DecoratedWith(RechargeMapstructMapperDecorator.class)
public interface RechargeMapstructMapper {
    @Mappings({
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "paymentMode", target = "paymentMode"),
    })
    SingleRechargeRequestDto rechargeToRechargeDto(SingleRechargeRequest request);

    @Mappings({
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(target = "paymentMode",ignore = true),
            @Mapping(target = "serviceCode", ignore = true),
            @Mapping(target = "serviceId", ignore = true),
            @Mapping(target = "authorizationUrl", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "closed", ignore = true),
            @Mapping(target = "paymentId", ignore = true),
    })
    SingleRechargeRequest rechargeDtoToRecharge(SingleRechargeRequestDto dto);

    @Mappings({
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "paymentMode", target = "paymentMode")
    })
    SingleRechargeRequestDto scheduledToSingle(ScheduledRechargeRequestDto dto);
}
