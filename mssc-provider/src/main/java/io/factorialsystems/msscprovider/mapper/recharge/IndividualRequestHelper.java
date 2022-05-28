package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.cache.ParameterCache;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.dto.ExtraDataPlanDto;
import io.factorialsystems.msscprovider.dto.ExtraPlanRequestDto;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ExtraDataEnquiry;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.recharge.ringo.response.ProductItem;
import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndividualRequestHelper {
    private final FactoryProducer producer;
    private final ParameterCache parameterCache;
    private final ServiceActionMapper serviceActionMapper;

    public BigDecimal checkRequests(List<IndividualRequest> requests) {

        return requests.stream()
                .map(this::processIndividualRequest)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal processIndividualRequest(IndividualRequest individualRequest) {

        String serviceCode = individualRequest.getServiceCode();

        if (serviceCode == null) {
            throw new RuntimeException("ServiceCode Not specified in Recharge Request");
        }

        ServiceAction action = serviceActionMapper.findByCode(serviceCode);

        if (action == null) {
            throw new RuntimeException(String.format("Invalid ServiceCode (%s), unable to find appropriate service", serviceCode));
        }

        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(action.getId());

        String serviceAction = null;
        String rechargeProviderCode = null;

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            rechargeProviderCode = parameter.getRechargeProviderCode();
            serviceAction = parameter.getServiceAction();
        } else {
            throw new RuntimeException(String.format("Unable to Load RechargeFactoryParameters for (%s)", individualRequest.getServiceCode()));
        }

        AbstractFactory factory = producer.getFactory(rechargeProviderCode);

        if (factory == null) {
            throw new RuntimeException(String.format("Unable to get Factory for Request (%s), Please ensure factories are configured appropriately", individualRequest.getServiceCode()));
        }

        individualRequest.setServiceId(action.getId());
        individualRequest.setServiceCode(action.getServiceCode());

        if (individualRequest.getServiceCost() != null && individualRequest.getProductId() != null) {
            log.error(String.format("Cost and ProductId set for Recipient (%s) Cost: (%.2f), ProductId: (%s) ", individualRequest.getRecipient(), individualRequest.getServiceCost(), individualRequest.getProductId()));
            throw new RuntimeException("Error you have specified a cost and a plan, cost for plans are determined by the system and not submitted");
        }

        if (individualRequest.getProductId() != null) {
            DataEnquiry enquiry  = factory.getPlans(serviceAction);

            if (enquiry == null) {
                ExtraDataEnquiry extraDataEnquiry = factory.getExtraPlans(serviceAction);

                if (extraDataEnquiry != null) {
                    ExtraDataPlanDto extraDataPlanDto = extraDataEnquiry.getExtraPlans (
                            ExtraPlanRequestDto.builder()
                                    .recipient(individualRequest.getRecipient())
                                    .serviceCode(individualRequest.getServiceCode())
                                    .build()
                    );

                    Integer price = extraDataPlanDto.getObject().stream()
                            .filter(r -> r.getCode().equals(individualRequest.getProductId()))
                            .findFirst()
                            .map(ProductItem::getPrice)
                            .orElseThrow(RuntimeException::new);

                    if  (price > 0) {
                        individualRequest.setServiceCost(new BigDecimal(price));
                    }
                }
            } else {
                DataPlanDto planDto = enquiry.getPlan(individualRequest.getProductId());
                individualRequest.setServiceCost(new BigDecimal(planDto.getPrice()));
            }
        }

        if (individualRequest.getServiceCost() == null) {
            throw new RuntimeException(String.format("Unable to determine Price of Recharge Request (%s) by (%s)", individualRequest.getId(), K.getUserName()));
        }

        if (!factory.getCheck(serviceAction).check(toSingleRechargeRequest(individualRequest))) {
            final String errorMessage = "Invalid or Insufficient Parameters for One of the IndividualRequests";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        return individualRequest.getServiceCost();
    }

    private SingleRechargeRequest toSingleRechargeRequest(IndividualRequest individualRequest) {

        return SingleRechargeRequest.builder()
                .recipient(individualRequest.getRecipient())
                .productId(individualRequest.getProductId())
                .serviceCode(individualRequest.getServiceCode())
                .serviceCost(individualRequest.getServiceCost())
                .serviceId(individualRequest.getServiceId())
                .build();
    }
}
