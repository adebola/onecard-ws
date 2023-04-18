package io.factorialsystems.msscpayments.external.client;

import io.factorialsystems.msscpayments.config.FeignConfig;
import io.factorialsystems.msscpayments.dto.RefundRequestDto;
import io.factorialsystems.msscpayments.dto.RefundResponseDto;
import io.factorialsystems.msscpayments.payment.wallet.InitializeWalletTransactionRequest;
import io.factorialsystems.msscpayments.payment.wallet.InitializeWalletTransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "wallet-server", configuration = FeignConfig.class)
public interface AccountClient {
    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/account",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    InitializeWalletTransactionResponse initializeWalletTransaction(@RequestBody InitializeWalletTransactionRequest txRequest);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/account/refund/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    RefundResponseDto refundPayment(@PathVariable("id") String id, @RequestBody RefundRequestDto dto);
}
