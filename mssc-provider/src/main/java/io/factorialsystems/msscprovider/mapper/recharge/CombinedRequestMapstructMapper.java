package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.CombinedRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
@DecoratedWith(CombinedRequestMapstructMapperDecorator.class)
public interface CombinedRequestMapstructMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "failed", target = "failed"),
            @Mapping(source = "retryId", target = "retryId"),
            @Mapping(source = "refundId", target = "refundId"),
            @Mapping(source = "resolveId", target = "resolveId"),
    })
    CombinedRechargeRequest singleToCombined(SingleRechargeRequest request);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "serviceCode", target = "serviceCode"),
            @Mapping(source = "productId", target = "productId"),
            @Mapping(source = "recipient", target = "recipient"),
            @Mapping(source = "serviceCost", target = "serviceCost"),
            @Mapping(source = "failed", target = "failed"),
            @Mapping(source = "retryId", target = "retryId"),
            @Mapping(source = "refundId", target = "refundId"),
            @Mapping(source = "resolveId", target = "resolveId"),
    })
    CombinedRechargeRequest individualToCombined(IndividualRequest individualRequest);

}
