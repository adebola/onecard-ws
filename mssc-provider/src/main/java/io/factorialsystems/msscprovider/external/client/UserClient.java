package io.factorialsystems.msscprovider.external.client;

import io.factorialsystems.msscprovider.config.FeignConfig;
import io.factorialsystems.msscprovider.dto.UserEntryListDto;
import io.factorialsystems.msscprovider.dto.UserIdListDto;
import io.factorialsystems.msscprovider.dto.user.SimpleUserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "user-server", configuration = FeignConfig.class)
public interface UserClient {
    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/user/simple/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    SimpleUserDto getUserById(@PathVariable("id") final String id);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/user/usernames",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    UserEntryListDto getUserEntries (@RequestBody UserIdListDto dto);
}