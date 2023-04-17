package io.factorialsystems.msscusers.external.client;

import io.factorialsystems.msscusers.config.FeignConfig;
import io.factorialsystems.msscusers.dto.AccountDto;
import io.factorialsystems.msscusers.dto.CreateAccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "wallet-server", configuration = FeignConfig.class)
public interface AccountClient {
    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/account/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    AccountDto getAccount(@PathVariable("id") String id);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/account/create",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    AccountDto createAccount(@RequestBody CreateAccountDto accountDto);
}
