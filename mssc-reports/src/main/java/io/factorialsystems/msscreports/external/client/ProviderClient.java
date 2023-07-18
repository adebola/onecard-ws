package io.factorialsystems.msscreports.external.client;

import io.factorialsystems.msscreports.config.FeignConfig;
import io.factorialsystems.msscreports.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "provider-server", configuration = FeignConfig.class)
public interface ProviderClient {
    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/recharge-report",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    CombinedRechargeList getRechargeReport(@RequestBody RechargeReportRequestDto dto);

    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/provider/recharge",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    PagedDto<RechargeProviderDto> getRechargeProviders(@RequestParam(value = "pageNumber") Integer pageNumber,
                                                       @RequestParam(value = "pageSize") Integer pageSize);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/recharge-report/short",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<RechargeProviderExpenditure> getShortProviderExpenditure(@RequestBody  RechargeProviderRequestDto dto);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/recharge-report/long",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<RechargeProviderExpenditure> getLongProviderExpenditure(@RequestBody RechargeProviderRequestDto dto);

    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/recharge-report/balances",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<RechargeProviderDto> getProviderBalances();
}
