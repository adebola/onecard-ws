package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.cache.ParameterCache;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.domain.rechargerequest.AutoIndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.recharge.DataPlanDto;
import io.factorialsystems.msscprovider.dto.recharge.ExtraDataPlanDto;
import io.factorialsystems.msscprovider.dto.recharge.ExtraPlanRequestDto;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.ExtraDataEnquiry;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.recharge.ringo.response.ProductItem;
import io.factorialsystems.msscprovider.utils.K;
import lombok.Builder;
import lombok.Data;
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

    @Data
    @Builder
    private static class InnerResults {
        private AbstractFactory factory;
        private String serviceAction;
    }

    public void checkAutoRequests(List<AutoIndividualRequest> requests) {
        requests.forEach(this::processAutoIndividualRequest);
    }

    public BigDecimal checkRequests(List<IndividualRequest> requests) {
        return requests.stream()
                .map(this::processIndividualRequest)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void processAutoIndividualRequest(AutoIndividualRequest autoRequest) {

        ServiceAction action = getAction(autoRequest.getServiceCode());
        InnerResults results = getFactory(action);

        autoRequest.setServiceId(action.getId());
        autoRequest.setServiceCode(action.getServiceCode());

        if (autoRequest.getServiceCost() != null && autoRequest.getProductId() != null) {
            log.error(String.format("Cost and ProductId set for Recipient (%s) Cost: (%.2f), ProductId: (%s) ", autoRequest.getRecipient(), autoRequest.getServiceCost(), autoRequest.getProductId()));
            throw new RuntimeException("Error you have specified a cost and a plan, cost for plans are determined by the system and not submitted");
        }

        final AbstractFactory factory = results.getFactory();
        final String serviceAction = results.getServiceAction();;

        if (autoRequest.getProductId() != null) {
            DataEnquiry enquiry  = factory.getPlans(serviceAction);
            Integer price =
                    checkPlanAndPrice(enquiry, results, autoRequest.getRecipient(), autoRequest.getServiceCode(), autoRequest.getProductId());
            autoRequest.setServiceCost(new BigDecimal(price));
        }

        if (autoRequest.getServiceCost() == null) {
            throw new RuntimeException(String.format("Unable to determine Price of Recharge Request (%s) by (%s)", autoRequest.getId(), K.getUserName()));
        }

        if (!factory.getCheck(serviceAction).check(toSingleRechargeRequest(autoRequest))) {
            final String errorMessage = "Invalid or Insufficient Parameters for One of the IndividualRequests";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
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
                DataPlanDto planDto = enquiry.getPlan(individualRequest.getProductId(), individualRequest.getServiceCode());
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

    private InnerResults getFactory(ServiceAction action) {
        List<RechargeFactoryParameters> parameters = parameterCache.getFactoryParameter(action.getId());

        String serviceAction = null;
        String rechargeProviderCode = null;

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            rechargeProviderCode = parameter.getRechargeProviderCode();
            serviceAction = parameter.getServiceAction();
        } else {
            throw new RuntimeException(String.format("Unable to Load RechargeFactoryParameters for (%s)", action.getServiceCode()));
        }

        AbstractFactory factory = producer.getFactory(rechargeProviderCode);

        if (factory == null) {
            throw new RuntimeException(String.format("Unable to get Factory for Request (%s), Please ensure factories are configured appropriately", action.getServiceCode()));
        }

        return InnerResults.builder()
                .factory(factory)
                .serviceAction(serviceAction)
                .build();
    }

    private ServiceAction getAction(String serviceCode) {

        if (serviceCode == null) {
            throw new RuntimeException("ServiceCode Not specified in Recharge Request");
        }

        ServiceAction action = serviceActionMapper.findByCode(serviceCode);

        if (action == null) {
            throw new RuntimeException(String.format("Invalid ServiceCode (%s), unable to find appropriate service", serviceCode));
        }

        return action;
    }

    private Integer checkPlanAndPrice(DataEnquiry enquiry, InnerResults results, String recipient,
                                      String serviceCode, String productId) {

        final AbstractFactory factory = results.getFactory();
        final String serviceAction = results.getServiceAction();

        Integer price = null;

        if (enquiry == null) {
            ExtraDataEnquiry extraDataEnquiry = factory.getExtraPlans(serviceAction);

            if (extraDataEnquiry != null) {
                ExtraDataPlanDto extraDataPlanDto = extraDataEnquiry.getExtraPlans (
                        ExtraPlanRequestDto.builder()
                                .recipient(recipient)
                                .serviceCode(serviceCode)
                                .build()
                );

                price = extraDataPlanDto.getObject().stream()
                        .filter(r -> r.getCode().equals(productId))
                        .findFirst()
                        .map(ProductItem::getPrice)
                        .orElseThrow(RuntimeException::new);
            }
        } else {
            DataPlanDto planDto = enquiry.getPlan(productId, serviceCode);
            price = Integer.valueOf(planDto.getPrice());
        }

        return price;
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

    private SingleRechargeRequest toSingleRechargeRequest(AutoIndividualRequest individualRequest) {

        return SingleRechargeRequest.builder()
                .recipient(individualRequest.getRecipient())
                .productId(individualRequest.getProductId())
                .serviceCode(individualRequest.getServiceCode())
                .serviceCost(individualRequest.getServiceCost())
                .serviceId(individualRequest.getServiceId())
                .build();
    }
}
