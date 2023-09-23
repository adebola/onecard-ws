package io.factorialsystems.msscreports.external.client;

import io.factorialsystems.msscreports.config.FeignConfig;
import io.factorialsystems.msscreports.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-server", configuration = FeignConfig.class)
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/user/nopage",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<SimpleUserDto> getAllUsers();

    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    PagedDto<UserDto> getUsers(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                               @RequestParam(value = "pageSize", required = false) Integer pageSize);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/user/usernames",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    UserEntryListDto getUserEntries (UserIdListDto dto);

    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/organization",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    PagedDto<OrganizationDto> getOrganizations(@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                               @RequestParam(value = "pageSize", required = false) Integer pageSize);

}
