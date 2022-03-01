package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.dao.SingleRechargeMapper;
import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.rechargerequest.BulkRechargeRequest;
import io.factorialsystems.msscprovider.domain.RechargeFactoryParameters;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.domain.rechargerequest.ScheduledRechargeRequest;
import io.factorialsystems.msscprovider.dto.BulkRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.DataPlanDto;
import io.factorialsystems.msscprovider.dto.ScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.recharge.DataEnquiry;
import io.factorialsystems.msscprovider.recharge.factory.AbstractFactory;
import io.factorialsystems.msscprovider.recharge.factory.FactoryProducer;
import io.factorialsystems.msscprovider.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class BulkRechargeMapstructMapperDecorator implements BulkRechargeMapstructMapper {
    private FactoryProducer producer;
    private RestTemplate restTemplate;
    private ServiceActionMapper serviceActionMapper;
    private SingleRechargeMapper singleRechargeMapper;
    private BulkRechargeMapstructMapper bulkRechargeMapstructMapper;

    @Value("${api.local.host.baseurl}")
    private String baseLocalUrl;

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

    @Autowired
    public void setBulkRechargeMapstructMapper(BulkRechargeMapstructMapper bulkRechargeMapstructMapper) {
        this.bulkRechargeMapstructMapper = bulkRechargeMapstructMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public BulkRechargeRequest rechargeDtoToRecharge(BulkRechargeRequestDto dto) {
        String serviceAction = null;
        String rechargeProviderCode = null;
        BulkRechargeRequest request = bulkRechargeMapstructMapper.rechargeDtoToRecharge(dto);

        request.setUserId(K.getUserId());

        // ServiceCode
        String serviceCode = dto.getServiceCode();

        if (serviceCode == null) {
            throw new RuntimeException("ServiceCode Not specified in Recharge Request");
        }

        ServiceAction action = serviceActionMapper.findByCode(serviceCode);

        if (action == null) {
            throw new RuntimeException(String.format("Invalid ServiceCode (%s), unable to find appropriate service", serviceCode));
        }

        List<RechargeFactoryParameters> parameters = singleRechargeMapper.factory(action.getId());

        if (parameters != null && !parameters.isEmpty()) {
            RechargeFactoryParameters parameter = parameters.get(0);
            rechargeProviderCode = parameter.getRechargeProviderCode();
            serviceAction = parameter.getServiceAction();
        } else {
            throw new RuntimeException(String.format("Unable to Load RechargeFactoryParameters for (%s)", dto.getServiceCode()));
        }

        AbstractFactory factory = producer.getFactory(rechargeProviderCode);

        if (factory == null) {
            throw new RuntimeException(String.format("Unable to get Factory for Request (%s), Please ensure factories are configured appropriately", dto.getServiceCode()));
        }

        request.setServiceId(action.getId());
        request.setServiceCode(action.getServiceCode());
        request.setTotalServiceCost(new BigDecimal(0));

        if (request.getServiceCost() != null && request.getProductId() != null) {
            throw new RuntimeException("Error you have specified a cost and a plan, plans have cost");
        }

        if (request.getProductId() != null) {
            DataEnquiry enquiry = factory.getPlans(serviceAction);
            DataPlanDto planDto = enquiry.getPlan(request.getProductId());
            BigDecimal cost = new BigDecimal(planDto.getPrice());
            request.setServiceCost(cost);
        }

        if (request.getServiceCost() == null) {
            throw new RuntimeException("No cost associated with this request either specify a cost or a plan that has a cost");
        }

        final String userId = K.getUserId();
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

        // GroupId
        if (dto.getGroupId() != null) {
            Integer count =
                    restTemplate.getForObject(baseLocalUrl + "/api/v1/beneficiary/length/" + dto.getGroupId(), Integer.class);

            if (count != null && count > 0) {
                request.setTotalServiceCost(dto.getServiceCost().multiply(new BigDecimal(count)));
                request.setGroupId(dto.getGroupId());
            }
        }

        // Recipient
        if (dto.getRecipients() != null && dto.getRecipients().length > 0) {
            int recipientCount = dto.getRecipients().length;
            double cost = request.getServiceCost().doubleValue() * recipientCount;
            BigDecimal currentCost = request.getTotalServiceCost();
            request.setTotalServiceCost(currentCost.add(new BigDecimal(cost)));
        }

        return request;
    }

    @Override
    public BulkRechargeRequestDto scheduledToBulkRechargeDto(ScheduledRechargeRequestDto dto) {
        return bulkRechargeMapstructMapper.scheduledToBulkRechargeDto(dto);
    }

    @Override
    public BulkRechargeRequest scheduledToBulkRecharge(ScheduledRechargeRequest request) {
        return bulkRechargeMapstructMapper.scheduledToBulkRecharge(request);
    }
}
