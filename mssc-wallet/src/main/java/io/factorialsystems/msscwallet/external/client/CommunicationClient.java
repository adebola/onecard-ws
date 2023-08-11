package io.factorialsystems.msscwallet.external.client;

import io.factorialsystems.msscwallet.config.FeignConfig;
import io.factorialsystems.msscwallet.dto.MailMessageDto;
import io.factorialsystems.msscwallet.dto.SMSMessageDto;
import io.factorialsystems.msscwallet.dto.SMSResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "communication-server", configuration = FeignConfig.class)
public interface CommunicationClient {

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/mail",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    String sendMailWithoutAttachment(@RequestBody MailMessageDto dto);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/sms",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    SMSResponseDto sendSMS(@RequestBody SMSMessageDto dto);
}
