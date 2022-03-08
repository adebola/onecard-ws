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
            @Mapping(source = "recipients", target = "recipients")
    })
    NewBulkRechargeRequest rechargeDtoToRecharge(NewBulkRechargeRequestDto dto);

    @Mappings({
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
    })
    IndividualRequest individualDtoToIndividual(IndividualRequestDto dto);
    List<IndividualRequest> listIndividualDtoToIndividual(List<IndividualRequestDto> dtos);
}