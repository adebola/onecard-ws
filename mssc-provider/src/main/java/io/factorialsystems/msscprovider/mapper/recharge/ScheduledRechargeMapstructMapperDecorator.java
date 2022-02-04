package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.dao.ServiceActionMapper;
import io.factorialsystems.msscprovider.domain.ServiceAction;
import io.factorialsystems.msscprovider.domain.rechargerequest.ScheduledRechargeRequest;
import io.factorialsystems.msscprovider.dto.ScheduledRechargeRequestDto;
import io.factorialsystems.msscprovider.utils.K;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

@Slf4j
public class ScheduledRechargeMapstructMapperDecorator implements ScheduledRechargeMapstructMapper {
    private ServiceActionMapper serviceActionMapper;
    private ScheduledRechargeMapstructMapper mapstructMapper;

    @Autowired
    public void setMapstructMapper(ScheduledRechargeMapstructMapper mapstructMapper) {
        this.mapstructMapper = mapstructMapper;
    }

    @Autowired
    public void setServiceActionMapper(ServiceActionMapper serviceActionMapper) {
        this.serviceActionMapper = serviceActionMapper;
    }

    @Override
    public ScheduledRechargeRequest rechargeDtoToRecharge(ScheduledRechargeRequestDto dto) {
        ScheduledRechargeRequest request = mapstructMapper.rechargeDtoToRecharge(dto);

        // ServiceCode
        String serviceCode = dto.getServiceCode();

        if (serviceCode == null) {
            throw new RuntimeException("ServiceCode Not specified in Scheduled Recharge Request");
        }

        ServiceAction action = serviceActionMapper.findByCode(serviceCode);

        if (action == null) {
            throw new RuntimeException(String.format("Invalid ServiceCode (%s), unable to find appropriate service", serviceCode));
        }

        request.setServiceId(action.getId());

        // We must ensure that for Fixed Priced services such as data Plans, the user must not specify a Price
        // We must also ensure for Variable Priced Services such as airtime the user must specify the Price

        if (action.getServiceCost() != null && request.getServiceCost() != null) {
            throw new RuntimeException(String.format("ServiceCost or Price set for service with fixed cost (%s)", action.getServiceCode()));
        }

        if (action.getServiceCost() != null) {
            request.setServiceCost(action.getServiceCost());
        }

        // PaymentMode
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

        // Recharge Type
        String recharge =
                Arrays.stream(K.ALL_RECHARGE_MODES).filter(x -> x.equals(dto.getRechargeType()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException(String.format("Invalid Recharge Type (%s)", dto.getRechargeType())));

        if (dto.getRechargeType().equals(K.SINGLE_RECHARGE)) {
            request.setRequestType(1);
        } else {
            request.setRequestType(1);
        }

        // Scheduled date
        Date date = dto.getScheduledDate();
        if (!date.after(new Date())) {
            throw new RuntimeException(String.format("Date (%s) is in the past or too near to be scheduled, please run instantly",
                    date.toString()));
        }

        request.setScheduledDate(new Timestamp(date.getTime()));

        return request;
    }
}
