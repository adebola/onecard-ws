package io.factorialsystems.msscpayments.payment;

import io.factorialsystems.msscpayments.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFactory {
    private final ApplicationContext applicationContext;

    public Payment getPaymentHelper(String paymentType) {
        if (Constants.PAYSTACK_PAY_MODE.equals(paymentType)) {
            return applicationContext.getBean(PaystackHelper.class);
        } else if (Constants.WALLET_PAY_MODE.equals(paymentType)) {
            return applicationContext.getBean(WalletHelper.class);
        } else {
            throw new RuntimeException(String.format("Unable to Load Helper for PaymentMode (%s)", paymentType));
        }
    }
}
