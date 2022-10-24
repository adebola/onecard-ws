package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.ScheduledRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.ScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.mapper.DateMapper;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(RechargeMapstructMapperDecorator.class)
public interface RechargeMapstructMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "paymentMode", target = "paymentMode"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "refundId", target = "refundId"),
            @Mapping(source = "retryId", target = "retryId"),
            @Mapping(source = "resolveId", target = "resolveId"),
            @Mapping(source = "failed", target = "failed"),
            @Mapping(source = "results", target = "results")
    })
    SingleRechargeRequestDto rechargeToRechargeDto(SingleRechargeRequest request);
    List<SingleRechargeRequestDto> listRechargeToRechargeDto(List<SingleRechargeRequest> requests);

    @Mappings({
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "accountType", target = "accountType"),
            @Mapping(source = "name", target = "name"),
            @Mapping(target = "paymentMode",ignore = true),
            @Mapping(target = "serviceCode", ignore = true),
            @Mapping(target = "serviceId", ignore = true),
            @Mapping(target = "authorizationUrl", ignore = true),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "closed", ignore = true),
            @Mapping(target = "paymentId", ignore = true),
            @Mapping(target = "results", ignore = true),
            @Mapping(target = "rechargeProviderId", ignore = true)
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
    SingleRechargeRequestDto scheduledToSingleRechargeDto(ScheduledRechargeRequestDto dto);

    @Mappings({
            @Mapping(source = "serviceId", target = "serviceId"),
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "authorizationUrl", target = "authorizationUrl"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "paymentId", target = "paymentId"),
            @Mapping(source = "paymentMode", target = "paymentMode"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "message", target = "message")
    })
    SingleRechargeRequest scheduleToSingleRecharge(ScheduledRechargeRequest request);
}