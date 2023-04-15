package io.factorialsystems.msscwallet.external.client;

import io.factorialsystems.msscwallet.config.FeignConfig;
import io.factorialsystems.msscwallet.dto.MailMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@FeignClient(value = "communication-server", configuration = FeignConfig.class)
public interface MailClient {

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/mail",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public String sendMailWithoutAttachment(@Valid @RequestBody MailMessageDto dto);
}
