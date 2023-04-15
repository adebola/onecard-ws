package io.factorialsystems.msscwallet.external.client;

import io.factorialsystems.msscwallet.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.factorialsystems.msscwallet.dto.PaymentRequestDto;
@FeignClient(value = "payment-server", configuration = FeignConfig.class)
public interface PaymentClient {

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/payment",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    PaymentRequestDto makePayment(PaymentRequestDto dto);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/pay",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    PaymentRequestDto initializePayment(PaymentRequestDto dto);

    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/pay/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    PaymentRequestDto checkPayment(@PathVariable("id") final String id);
}
