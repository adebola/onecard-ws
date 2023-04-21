package io.factorialsystems.msscprovider.external.client;

import io.factorialsystems.msscprovider.config.FeignConfig;
import io.factorialsystems.msscprovider.dto.MailMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "communication-server", configuration = FeignConfig.class, contextId = "json")
public interface CommunicationJSONClient {
    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/mail",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    String sendMailWithoutAttachment(@RequestBody MailMessageDto dto);
}
