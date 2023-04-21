package io.factorialsystems.msscprovider.external.client;

import io.factorialsystems.msscprovider.config.CommunicationFeignConfig;
import io.factorialsystems.msscprovider.dto.MailMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "communication-server", configuration = CommunicationFeignConfig.class, contextId = "multipart")
public interface CommunicationClient {

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/mail/attachment",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    String sendMailWithAttachment(@RequestPart(value = "message") MailMessageDto dto, @RequestPart(value = "file") MultipartFile file);

    @RequestMapping(method = RequestMethod.POST,
            value = "/api/v1/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    String uploadFile(@RequestPart(value = "file") MultipartFile file);
}


