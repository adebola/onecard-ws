package io.factorialsystems.msscprovider.external.client;

import io.factorialsystems.msscprovider.config.ImpersonateFeignConfig;
import io.factorialsystems.msscprovider.dto.payment.PaymentRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "payment-server", configuration = ImpersonateFeignConfig.class, contextId = "impersonate-user")
public interface ImpersonatePaymentClient {
    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/payment",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    PaymentRequestDto makePayment(PaymentRequestDto dto);
}
