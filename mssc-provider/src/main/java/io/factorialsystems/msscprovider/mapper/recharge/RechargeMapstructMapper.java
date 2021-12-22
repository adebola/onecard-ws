package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.RechargeRequest;
import io.factorialsystems.msscprovider.dto.RechargeRequestDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
@DecoratedWith(RechargeMapstructMapperDecorator.class)
public interface RechargeMapstructMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "authorizationUrl", target = "authorizationUrl"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "productId", target = "productId")
    })
    RechargeRequestDto rechargeToRechargeDto(RechargeRequest request);

    @Mappings({
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(target = "serviceCode", ignore = true),
            @Mapping(target = "serviceId", ignore = true),
            @Mapping(target = "authorizationUrl", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "closed", ignore = true),
            @Mapping(target = "paymentId", ignore = true)
    })
    RechargeRequest rechargeDtoToRecharge(RechargeRequestDto dto);
}
