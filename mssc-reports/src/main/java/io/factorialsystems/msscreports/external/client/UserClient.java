package io.factorialsystems.msscreports.external.client;

import io.factorialsystems.msscreports.config.FeignConfig;
import io.factorialsystems.msscreports.dto.SimpleUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "user-server", configuration = FeignConfig.class)
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/user/nopage",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<SimpleUserDto> getAllUsers();
}
