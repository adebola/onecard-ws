package io.factorialsystems.msscprovider.mapper.recharge;

import io.factorialsystems.msscprovider.utils.K;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class PaymentModeHelper {

    public String checkPaymentMode(String paymentMode) {

        String finalMode = null;
        final String userId = K.getUserId();

        if (paymentMode == null) { // No Payment Mode Specified
            if (userId == null) { // Anonymous User Not Logged On
                finalMode = K.PAYSTACK_PAY_MODE;
            } else {
                finalMode = K.WALLET_PAY_MODE;
            }
        } else {
            finalMode = Arrays.stream(K.ALL_PAYMENT_MODES).filter(x -> x.equals(paymentMode))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Invalid PaymentMode String (%s)", paymentMode)));

            // Specified Wallet but Not Logged In
            if (paymentMode.equals(K.WALLET_PAY_MODE) && userId == null) {
                throw new RuntimeException("You must be logged In to do a Wallet purchase, please login or choose and alternate payment method");
            }
        }

        return finalMode;
    }
}
