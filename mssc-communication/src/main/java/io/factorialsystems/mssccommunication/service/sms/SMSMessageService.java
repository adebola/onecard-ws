package io.factorialsystems.mssccommunication.service.sms;

import io.factorialsystems.mssccommunication.document.SMSMessage;
import io.factorialsystems.mssccommunication.dto.PagedDto;
import io.factorialsystems.mssccommunication.dto.SMSMessageDto;
import io.factorialsystems.mssccommunication.dto.SMSResponseDto;
import io.factorialsystems.mssccommunication.mapper.SMSMessageMapper;
import io.factorialsystems.mssccommunication.repository.SMSMessageRepository;
import io.factorialsystems.mssccommunication.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSMessageService {
    @Value("${SMS_URL}")
    private String smsUrl;

    @Value("${SMS_USER}")
    private String smsUser;

    @Value("${SMS_PASSWORD}")
    private String smsPassword;

    public static final String SMS_TAG = "ONECARD";

    private final SMSMessageMapper smsMessageMapper;
    private final SMSMessageRepository smsMessageRepository;

    @Transactional
    public SMSResponseDto sendMessage(SMSMessageDto dto) {
        SMSMessage message = smsMessageMapper.smsDtoToSMS(dto);
        RestTemplate restTemplate = new RestTemplate();

        String newTo = null;
        final String to = message.getTo();

        try {
            if (to.indexOf('0') == 0) {
                newTo = "234" + to.substring(1);
            } else if (to.indexOf("+") == 0) {
                newTo = to.substring(1);
            } else if (to.substring(0, 3).compareTo("234") == 0) {
                newTo = to;
            } else {
                final String s = String.format("Invalid msisdn format %s", to);
                log.error(s);
                return SMSResponseDto.builder()
                        .status(false)
                        .message(s)
                        .build();
            }
        } catch (Exception ex) {
            log.error("Invalid msisdn format exception {}", ex.getMessage());
            return SMSResponseDto.builder()
                    .status(false)
                    .message(String.format("Invalid msisdn format %s", to))
                    .build();
        }

        log.info(String.format("Sending Message to %s", newTo));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("user", smsUser);
        param.add("pass", smsPassword);
        param.add("from", SMS_TAG);
        param.add("to", newTo);
        param.add("msg", dto.getMessage());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(param, headers);

        message.setUserId(K.getUserId());
        message.setCreatedDate(new Date());
        message.setSentBy(K.getEmail());

        String responseMessage = null;

        try {
            ResponseEntity<String> response = restTemplate.postForEntity( smsUrl, request , String.class );
            responseMessage =  response.getBody();
            final boolean status = "sent".equals(responseMessage);

            log.info(String.format("SMS Response is %s", response.getBody()));

            message.setResponse(responseMessage);
            message.setStatus(status);
            final SMSMessage save = smsMessageRepository.save(message);

            return SMSResponseDto.builder()
                    .id(save.getId())
                    .status(status)
                    .message(status ? "Message Sent Successfully" : "Message Send Failed")
                    .build();
        } catch (Exception ex) {
            log.error(String.format("Error sending sms to %s Reason: %s", message.getTo(), ex.getMessage()));
        }

        return SMSResponseDto.builder()
                .status(false)
                .message(String.format("Error sending SMS to %s", message.getTo()))
                .build();
    }

    public PagedDto<SMSMessageDto> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<SMSMessage> messages = smsMessageRepository.findAll(pageable);
        return createdDtos(messages);
    }

    public PagedDto<SMSMessageDto> findByUserId(String userId, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<SMSMessage> messages = smsMessageRepository.findByUserId(pageable, userId);
        return createdDtos(messages);
    }

    private PagedDto<SMSMessageDto> createdDtos(Page<SMSMessage> messages) {
        PagedDto<SMSMessageDto> pagedDto = new PagedDto<>();

        pagedDto.setList(smsMessageMapper.listSMSToSMSDto(messages.toList()));
        pagedDto.setPages(messages.getTotalPages());
        pagedDto.setPageNumber(messages.getNumber());
        pagedDto.setPageSize(messages.getSize());
        pagedDto.setTotalSize((int) messages.getTotalElements());

        return pagedDto;
    }
}