package io.factorialsystems.msscreports.external.client;

import io.factorialsystems.msscreports.config.FeignConfig;
import io.factorialsystems.msscreports.dto.CombinedRechargeList;
import io.factorialsystems.msscreports.dto.RechargeReportRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "provider-server", configuration = FeignConfig.class)
public interface ProviderClient {
    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/recharge-report",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    CombinedRechargeList getRechargeReport(@RequestBody RechargeReportRequestDto dto);
}
