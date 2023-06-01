package io.factorialsystems.msscreports.external.client;


import io.factorialsystems.msscreports.config.FeignConfig;
import io.factorialsystems.msscreports.dto.FundWalletRequestDto;
import io.factorialsystems.msscreports.dto.WalletReportRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "wallet-server", configuration = FeignConfig.class)
public interface AccountClient {
    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/account/report",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<FundWalletRequestDto> getWalletFunding(WalletReportRequestDto dto);
}