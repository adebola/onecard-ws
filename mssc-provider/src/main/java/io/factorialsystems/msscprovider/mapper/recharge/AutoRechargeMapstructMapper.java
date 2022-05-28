package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.AutoRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.dto.AutoRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.AutoUploadFileRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.IndividualRequestDto;
import io.factorialsystems.msscprovider.mapper.DateMapper;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(AutoRechargeMapstructMapperDecorator.class)
public interface AutoRechargeMapstructMapper {
    @Mappings({
            @Mapping(target = "startDate", ignore = true),
            @Mapping(source = "endDate", target = "endDate"),
            @Mapping(target = "paymentMode", ignore = true),
            @Mapping(source = "recipients", target = "recipients"),
            @Mapping(source = "title", target = "title"),
    })
    AutoRechargeRequest dtoToRequest(AutoRechargeRequestDto dto);

    @Mappings({
            @Mapping(source = "paymentMode", target = "paymentMode"),
            @Mapping(source = "recipients", target = "recipients"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "startDate", target = "startDate"),
            @Mapping(source = "endDate", target = "endDate")
    })
    AutoRechargeRequestDto requestToDto(AutoRechargeRequest request);

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
    })
    IndividualRequestDto individualToIndividualDto(IndividualRequest request);
    List<IndividualRequestDto> listIndividualToIndividualDto(List<IndividualRequest> requests);

    @Mappings({
            @Mapping(source = "paymentMode", target = "paymentMode"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "startDate", target = "startDate"),
            @Mapping(source = "endDate", target = "endDate"),
            @Mapping(source = "daysOfWeek", target = "daysOfWeek"),
            @Mapping(source = "daysOfMonth", target = "daysOfMonth"),
            @Mapping(target = "recipients",ignore = true),
    })
    AutoRechargeRequestDto uploadToRechargeRequestDto(AutoUploadFileRechargeRequestDto dto);
}
