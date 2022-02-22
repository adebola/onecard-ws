package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.domain.rechargerequest.ScheduledRechargeRequest;
import io.factorialsystems.msscprovider.domain.rechargerequest.SingleRechargeRequest;
import io.factorialsystems.msscprovider.dto.ScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.utils.K;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class RechargeMapstructMapperDecorator implements RechargeMapstructMapper {
    private ServiceActionMapper serviceActionMapper;
    private RechargeMapstructMapper rechargeMapstructMapper;

    @Autowired
    public void setServiceActionMapper(ServiceActionMapper serviceActionMapper) {
        this.serviceActionMapper = serviceActionMapper;
    }

    @Autowired
    public void setRechargeMapstructMapper(RechargeMapstructMapper rechargeMapstructMapper) {
        this.rechargeMapstructMapper = rechargeMapstructMapper;
    }

    @Override
    public SingleRechargeRequestDto rechargeToRechargeDto(SingleRechargeRequest request) {
        return rechargeMapstructMapper.rechargeToRechargeDto(request);
    }

    @Override
    public SingleRechargeRequest rechargeDtoToRecharge(SingleRechargeRequestDto dto) {

        SingleRechargeRequest request = rechargeMapstructMapper.rechargeDtoToRecharge(dto);
        request.setUserId(K.getUserId());

        String serviceCode = dto.getServiceCode();

        if (serviceCode == null) {
            throw new RuntimeException("ServiceCode Not specified in Recharge Request");
        }

        ServiceAction action = serviceActionMapper.findByCode(serviceCode);

        if (action == null) {
            throw new RuntimeException(String.format("Invalid ServiceCode (%s), unable to find appropriate service", serviceCode));
        }

        request.setServiceId(action.getId());
        request.setServiceCode(action.getServiceCode());

        // We must ensure that for Fixed Priced services such as data Plans, the user must not specify a Price
        // We must also ensure for Variable Priced Services such as airtime the user must specify the Price

        if (action.getServiceCost() != null && request.getServiceCost() != null) {
            throw new RuntimeException(String.format("ServiceCost or Price set for service with fixed cost (%s)", action.getServiceCode()));
        }

        if (action.getServiceCost() != null) {
            request.setServiceCost(action.getServiceCost());
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

        return request;
    }

    @Override
    public SingleRechargeRequestDto scheduledToSingleRechargeDto(ScheduledRechargeRequestDto dto) {
        return rechargeMapstructMapper.scheduledToSingleRechargeDto(dto);
    }

    @Override
    public SingleRechargeRequest scheduleToSingleRecharge(ScheduledRechargeRequest request) {
        return rechargeMapstructMapper.scheduleToSingleRecharge(request);
    }
}
