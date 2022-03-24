package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.domain.rechargerequest.IndividualRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewBulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.NewScheduledRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.dto.NewScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
public class NewScheduledRechargeMapstructMapperDecorator implements NewScheduledRechargeMapstructMapper {

    private FactoryProducer producer;
    private ServiceActionMapper serviceActionMapper;
    private SingleRechargeMapper singleRechargeMapper;
    private NewScheduledRechargeMapstructMapper mapstructMapper;

    private static final Integer SINGLE_RECHARGE = 1;
    private static final Integer BULK_RECHARGE = 2;

    @Autowired
    public void setBulkRechargeMapstructMapper(NewScheduledRechargeMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Autowired
    public void setFactoryProducer(FactoryProducer producer) {
        this.producer = producer;
    }

    @Autowired
    public void setServiceActionMapper(ServiceActionMapper serviceActionMapper) {
        this.serviceActionMapper = serviceActionMapper;
    }

    @Autowired
    public void setRechargeMapper(SingleRechargeMapper singleRechargeMapper) {
        this.singleRechargeMapper = singleRechargeMapper;
    }

    @Override
    public NewScheduledRechargeRequest rechargeDtoToRecharge(NewScheduledRechargeRequestDto dto) {
        NewScheduledRechargeRequest request = mapstructMapper.rechargeDtoToRecharge(dto);

        final String userId = K.getUserId();
        request.setUserId(userId);
        request.setTotalServiceCost(new BigDecimal(0));

        // PaymentMode
        final String paymentMode = dto.getPaymentMode();

        if (paymentMode == null) { // No Payment Mode Specified
            if (userId == null) { // Anonymous User Not Logged On
                request.setPaymentMode(K.PAYSTACK_PAY_MODE);
            } else {
                request.setPaymentMode(K.WALLET_PAY_MODE);
            }
        } else {
            String mode
                    = Arrays.stream(K.ALL_PAYMENT_MODES).filter(x -> x.equals(paymentMode))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Invalid PaymentMode String (%s)", paymentMode)));

            // Specified Wallet but Not Logged In
            if (paymentMode.equals(K.WALLET_PAY_MODE) && userId == null) {
                throw new RuntimeException("You must be logged In to do a Wallet purchase, please login or choose and alternate payment method");
            }

            request.setPaymentMode(mode);
        }

        // RequestType 'single' or 'bulk'
        if (dto.getRechargeType().equals(K.SINGLE_RECHARGE)) {
            request.setRequestType(SINGLE_RECHARGE);
        } else {
            request.setRequestType(BULK_RECHARGE);
        }

        // Scheduled date make sure not in the past or toonear
        Date date = dto.getScheduledDate();
        if (!date.after(new Date())) {
            throw new RuntimeException(String.format("Date (%s) is in the past or too near to be scheduled, please run instantly",
                    date.toString()));
        }

        request.setScheduledDate(new Timestamp(date.getTime()));

        // Go through IndividualRequests and Validate them, Scheduled Recharge treats
        // Scheduled Bulk and Scheduled Single similarly, Single is just Bulk with 1 IndividualRequest
        List<IndividualRequest> individualRequests = request.getRecipients();
        individualRequests.forEach(individualRequest -> {

            // Validate Service Code
            String serviceCode = individualRequest.getServiceCode();

            if (serviceCode == null) {
                throw new RuntimeException("ServiceCode Not specified in Recharge Request");
            }

            ServiceAction action = serviceActionMapper.findByCode(serviceCode);

            if (action == null) {
                throw new RuntimeException(String.format("Invalid ServiceCode (%s), unable to find appropriate service", serviceCode));
            }

            // Get Recharge Parameters
            List<RechargeFactoryParameters> parameters = singleRechargeMapper.factory(action.getId());

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
                throw new RuntimeException("Error you have specified a cost and a plan, plans have cost");
            }

            if (individualRequest.getProductId() != null) {
                DataEnquiry enquiry = factory.getPlans(serviceAction);
                DataPlanDto planDto = enquiry.getPlan(individualRequest.getProductId());
                BigDecimal cost = new BigDecimal(planDto.getPrice());
                individualRequest.setServiceCost(cost);
            }

            BigDecimal currentCost = request.getTotalServiceCost();
            request.setTotalServiceCost(currentCost.add(individualRequest.getServiceCost()));

            if (!factory.getCheck(serviceAction).check(ToSingleRecharge(request, individualRequest))) {
                final String errorMessage = "Invalid or Insufficient Parameters for One of the IndividualRequests";
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        });

        return request;
    }

    @Override
    public NewBulkRechargeRequest ToBulkRechargeRequest(NewScheduledRechargeRequest request) {
        return mapstructMapper.ToBulkRechargeRequest(request);
    }

    private SingleRechargeRequest ToSingleRecharge(NewScheduledRechargeRequest newRequest, IndividualRequest request) {
        return SingleRechargeRequest.builder()
                .userId(newRequest.getUserId())
                .authorizationUrl(newRequest.getAuthorizationUrl())
                .redirectUrl(newRequest.getRedirectUrl())
                .recipient(request.getRecipient())
                .productId(request.getProductId())
                .serviceCode(request.getServiceCode())
                .serviceCost(request.getServiceCost())
                .serviceId(request.getServiceId())
                .build();
    }
}
