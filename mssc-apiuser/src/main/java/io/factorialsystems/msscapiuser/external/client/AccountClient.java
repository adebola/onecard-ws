package io.factorialsystems.msscapiuser.external.client;

import io.factorialsystems.msscapiuser.config.FeignConfig;
import io.factorialsystems.msscapiuser.dto.response.BalanceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "wallet-server", configuration = FeignConfig.class)
public interface AccountClient {
    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/account/balance",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    BalanceDto getAccountBalance();
}
