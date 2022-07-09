package io.factorialsystems.msscwallet.mapper;

import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import io.factorialsystems.msscwallet.dto.FundWalletRequestDto;
import io.factorialsystems.msscwallet.dto.FundWalletResponseDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
@DecoratedWith(FundWalletMapstructMapperDecorator.class)
public interface FundWalletMapstructMapper {

    @Mappings({
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "redirectUrl", target = "redirectUrl")
    })
    FundWalletRequest dtoToWalletRequest(FundWalletRequestDto dto);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "authorizationUrl", target = "authorizationUrl"),
            @Mapping(source = "redirectUrl", target = "redirectUrl")
    })
    FundWalletResponseDto requestToResponseDto(FundWalletRequest request);

    @Mappings({
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "status", target = "status"),
            @Mapping(source = "paymentVerified", target = "paymentVerified"),
            @Mapping(source = "message", target = "message"),
            @Mapping(source = "createdOn", target = "createdOn"),
            @Mapping(source = "closed", target = "closed"),
            @Mapping(source = "redirectUrl", target = "redirectUrl")
    })
    FundWalletRequestDto requestToRequestDto(FundWalletRequest request);
    List<FundWalletRequestDto> listRequestToRequestDto(List<FundWalletRequest> requests);
}
