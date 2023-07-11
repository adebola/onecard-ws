package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.domain.CombinedRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.domain.report.ReportIndividualRequest;
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
            @Mapping(source = "paymentMode", target = "paymentMode"),
            @Mapping(source = "rechargeProvider", target = "rechargeProvider"),
            @Mapping(target = "userName", ignore = true)
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
            @Mapping(target = "userName", ignore = true)
    })
    CombinedRechargeRequest individualToCombined(IndividualRequest individualRequest);

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
            @Mapping(source = "results", target = "results"),
            @Mapping(source = "createdAt", target = "createdAt"),
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "parentId", target = "parentId"),
            @Mapping(source = "paymentMode", target = "paymentMode"),
            @Mapping(target = "userName", ignore = true)
    })
    CombinedRechargeRequest reportIndividualToCombined(ReportIndividualRequest reportIndividualRequest);
}
