package io.factorialsystems.msscwallet.external.client;

import io.factorialsystems.msscwallet.config.FeignConfig;
import io.factorialsystems.msscwallet.dto.ServiceActionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "provider-server", configuration = FeignConfig.class)
public interface ProviderClient {
    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/serviceprovider/service/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ServiceActionDto getService(@PathVariable("id") Integer id);
}

