package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.rechargerequest.*;
import io.factorialsystems.msscprovider.dto.recharge.*;
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
    AutoIndividualRequest individualDtoToIndividual(AutoIndividualRequestDto dto);
    List<AutoIndividualRequest> listIndividualDtoToIndividual(List<AutoIndividualRequestDto> dtos);

    @Mappings({
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
    })
    AutoIndividualRequestDto individualToIndividualDto(AutoIndividualRequest request);
    List<AutoIndividualRequestDto> listIndividualToIndividualDto(List<AutoIndividualRequest> requests);



    @Mappings({
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
    })
    AutoIndividualRequestDto autoToNonAuto(IndividualRequest request);
    List<AutoIndividualRequestDto> listAutoToNonAuto(List<IndividualRequestDto> requests);

    @Mappings({
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "telephone", target = "telephone"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
    })
    IndividualRequestDto nonAutoToAuto(AutoIndividualRequest request);
    List<IndividualRequestDto> listNonAutoToAuto(List<AutoIndividualRequest> requests);


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


    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "startDate", target = "startDate"),
            @Mapping(source = "endDate", target = "endDate"),
            @Mapping(target = "recurringType", ignore = true),
    })
    ShortAutoRechargeRequestDto shortDtoToShort(ShortAutoRechargeRequest request);
    List<ShortAutoRechargeRequestDto> listShortDtoToShort(List<ShortAutoRechargeRequest> requests);
}
