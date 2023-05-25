package io.factorialsystems.msscwallet.external.client;

import io.factorialsystems.msscwallet.dto.SimpleUserDto;
import io.factorialsystems.msscwallet.dto.UserEntryListDto;
import io.factorialsystems.msscwallet.dto.UserIdListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@FeignClient(value = "user-server")
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