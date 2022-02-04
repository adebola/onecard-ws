package io.factorialsystems.msscwallet.mapper;

import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import io.factorialsystems.msscwallet.dto.FundWalletRequestDto;
import io.factorialsystems.msscwallet.dto.FundWalletResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
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
}


