package io.factorialsystems.msscusers.external.client;

import io.factorialsystems.msscusers.config.FeignConfig;
import io.factorialsystems.msscusers.dto.MailMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@FeignClient(value = "communication-server", configuration = FeignConfig.class)
public interface CommunicationClient {

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/mail",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    String sendMailWithoutAttachment(@Valid @RequestBody MailMessageDto dto);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    String uploadFile(@RequestPart(value = "file") MultipartFile file);
}
