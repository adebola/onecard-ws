package io.factorialsystems.msscapiuser.external.client;

import io.factorialsystems.msscapiuser.config.FeignConfig;
import io.factorialsystems.msscapiuser.dto.request.ExtraPlanRequestDto;
import io.factorialsystems.msscapiuser.dto.request.NewBulkRechargeRequestDto;
import io.factorialsystems.msscapiuser.dto.request.SingleRechargeRequestDto;
import io.factorialsystems.msscapiuser.dto.response.DataPlanDto;
import io.factorialsystems.msscapiuser.dto.response.ExtraDataPlanDto;
import io.factorialsystems.msscapiuser.dto.response.NewBulkRechargeResponseDto;
import io.factorialsystems.msscapiuser.dto.response.SingleRechargeResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "provider-server", configuration = FeignConfig.class)
public interface ProviderClient {
    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/auth-recharge/bulk",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    NewBulkRechargeResponseDto bulkRecharge(@RequestBody NewBulkRechargeRequestDto dto);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/auth-recharge",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    SingleRechargeResponseDto singleRecharge(@RequestBody SingleRechargeRequestDto dto);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/recharge/plans",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    ExtraDataPlanDto getExtraDataPlans(@RequestBody ExtraPlanRequestDto dto);

    @RequestMapping(method = RequestMethod.GET,
            value = "/api/v1/recharge/plans/{code}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    List<DataPlanDto> getDataPlans(@PathVariable("code") String code);
}
