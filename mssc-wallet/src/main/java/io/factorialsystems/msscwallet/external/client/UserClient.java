package io.factorialsystems.msscwallet.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.factorialsystems.msscwallet.dto.SimpleUserDto;
@FeignClient(value = "user-server")
public interface UserClient {
    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/user/simple/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    SimpleUserDto getUserById(@PathVariable("id") final String id);
}