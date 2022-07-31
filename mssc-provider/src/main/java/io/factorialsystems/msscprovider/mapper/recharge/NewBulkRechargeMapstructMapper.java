package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.dto.IndividualRequestDto;
import io.factorialsystems.msscprovider.dto.NewBulkRechargeRequestDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
@DecoratedWith(NewBulkRechargeMapstructMapperDecorator.class)
public interface NewBulkRechargeMapstructMapper {

    @Mappings({
            @Mapping(target = "paymentMode", ignore = true),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "recipients", target = "recipients"),
            @Mapping(source = "autoRequestId", target = "autoRequestId"),
    })
    NewBulkRechargeRequest rechargeDtoToRecharge(NewBulkRechargeRequestDto dto);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "paymentMode", target = "paymentMode"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "totalServiceCost", target = "totalServiceCost"),
            @Mapping(source = "autoRequestId", target = "autoRequestId"),
            @Mapping(target = "recipients", ignore = true)
    })
    NewBulkRechargeRequestDto rechargeToRechargDto(NewBulkRechargeRequest request);

    List<NewBulkRechargeRequestDto> listRechargeToRechargDto(List<NewBulkRechargeRequest> requests);

    @Mappings({
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
    })
    IndividualRequest individualDtoToIndividual(IndividualRequestDto dto);
    List<IndividualRequest> listIndividualDtoToIndividual(List<IndividualRequestDto> dtos);

    @Mappings({
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
            @Mapping(source = "failed", target = "failed"),
            @Mapping(source = "failedMessage", target = "failedMessage"),
            @Mapping(source = "refundId", target = "refundId"),
            @Mapping(source = "retryId", target = "retryId"),
            @Mapping(source = "resolveId", target = "resolveId")
    })
    IndividualRequestDto individualToIndividualDto(IndividualRequest request);
    List<IndividualRequestDto> listIndividualToIndividualDto(List<IndividualRequest> requests);
}