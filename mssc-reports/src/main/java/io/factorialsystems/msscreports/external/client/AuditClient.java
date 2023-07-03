package io.factorialsystems.msscreports.external.client;

import io.factorialsystems.msscreports.config.FeignConfig;
import io.factorialsystems.msscreports.dto.AuditMessageDto;
import io.factorialsystems.msscreports.dto.AuditSearchDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "audit-server", configuration = FeignConfig.class)
public interface AuditClient {
    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/audit/report",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<AuditMessageDto> getAudits(@RequestBody AuditSearchDto auditSearchDto);
}
