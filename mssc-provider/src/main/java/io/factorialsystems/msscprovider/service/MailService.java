package io.factorialsystems.msscprovider.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.config.JMSConfig;
import io.factorialsystems.msscprovider.dto.MailMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private static final String WITHOUT_ATTACHMENT_URL = "/api/v1/mail";
    private static final String WITH_ATTACHMENT_URL = "/api/v1/mail/attachment";

    @Value("${api.local.host.baseurl}")
    private String baseUrl;

    public String sendMailWithOutAttachment(MailMessageDto dto) {

        log.info(String.format("Sending Mail without attachment to %s", dto.getTo()));
        RestTemplate restTemplate = new RestTemplate();
        //restTemplate.getInterceptors().add(new RestTemplateInterceptor());

        return restTemplate.postForObject(baseUrl + WITHOUT_ATTACHMENT_URL, dto, String.class);
    }

    @SneakyThrows
    public void pushMailWithOutAttachment(MailMessageDto dto) {
        jmsTemplate.convertAndSend(JMSConfig.SEND_PROVIDER_MAIL_QUEUE, objectMapper.writeValueAsString(dto));
    }

    public String sendMailWithAttachment(FileSystemResource file, MailMessageDto dto) {
        log.info(String.format("Sending Mail with attachment to %s", dto.getTo()));
        RestTemplate restTemplate = new RestTemplate();
        //restTemplate.getInterceptors().add(new RestTemplateInterceptor());

        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("message", dto);
        requestBody.add("file", file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> formEntity = new HttpEntity<>(requestBody, headers);

        return restTemplate.postForObject(baseUrl + WITH_ATTACHMENT_URL, formEntity, String.class);
    }
}
