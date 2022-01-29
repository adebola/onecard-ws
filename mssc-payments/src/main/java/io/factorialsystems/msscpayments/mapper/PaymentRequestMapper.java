package io.factorialsystems.msscpayments.mapper;

import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface PaymentRequestMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "authorizationUrl", target = "authorizationUrl"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(source = "verified", target = "verified"),
            @Mapping(source = "message", target = "message"),
            @Mapping(source = "status", target = "status")
    })
    PaymentRequestDto requestToDto(PaymentRequest request);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "authorizationUrl", target = "authorizationUrl"),
            @Mapping(source = "redirectUrl", target = "redirectUrl"),
            @Mapping(target = "accessCode", ignore = true),
            @Mapping(target = "paymentCreated", ignore = true),
            @Mapping(target = "paymentVerified", ignore = true),
            @Mapping(target = "reference", ignore = true),
            @Mapping(target = "verified", ignore = true)
    })
    PaymentRequest requestDtoToRequest(PaymentRequestDto dto);
}
