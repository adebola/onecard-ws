package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.BulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.ScheduledRechargeRequest;
import io.factorialsystems.msscprovider.dto.BulkRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.ScheduledRechargeRequestDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
@DecoratedWith(BulkRechargeMapstructMapperDecorator.class)
public interface BulkRechargeMapstructMapper {

    @Mappings({
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "recipients", target = "recipients"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "paymentMode", target = "paymentMode"),
            @Mapping(target = "serviceCode", ignore = true),
            @Mapping(target = "serviceId", ignore = true),
            @Mapping(target = "groupId", ignore = true),
            @Mapping(target = "authorizationUrl", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "closed", ignore = true),
            @Mapping(target = "paymentId", ignore = true),
    })
    BulkRechargeRequest rechargeDtoToRecharge(BulkRechargeRequestDto dto);

    @Mappings({
            @Mapping(source = "serviceCode", target = "serviceCode"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "groupId", target = "groupId"),
            @Mapping(source = "recipients", target = "recipients"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "paymentMode", target = "paymentMode")
    })
    BulkRechargeRequestDto scheduledToBulkRechargeDto(ScheduledRechargeRequestDto dto);

    @Mappings({
            @Mapping(source = "serviceId", target = "serviceId"),
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "groupId", target = "groupId"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "authorizationUrl", target = "authorizationUrl"),
            @Mapping(source = "paymentMode", target = "paymentMode"),
            @Mapping(source = "paymentId", target = "paymentId"),
            @Mapping(source = "recipients", target = "recipients"),
            @Mapping(source = "totalServiceCost", target = "totalServiceCost")
    })
    BulkRechargeRequest scheduledToBulkRecharge(ScheduledRechargeRequest request);
}