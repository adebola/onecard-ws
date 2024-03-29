package io.factorialsystems.mssccommunication.service.sms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.mssccommunication.document.SMSMessage;
import io.factorialsystems.mssccommunication.dto.AsyncSMSMessageDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSMessageService {
    @Value("${sms.url}")
    private String smsUrl;

    @Value("${sms.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper;
    private final SMSMessageMapper smsMessageMapper;
    private final SMSMessageRepository smsMessageRepository;

    @Transactional
    public void asyncSendMessage(@Valid AsyncSMSMessageDto dto) {
        SMSMessage message = smsMessageMapper.asyncSMSDtoToSMS(dto);
        Optional<String> parseTo = parseTo(dto.getTo());

        if (parseTo.isEmpty()) {
            final String s = String.format("Invalid msisdn format %s",message.getTo());
            log.error(s);
            return;
        }

        final String newTo = parseTo.get();

        log.info(String.format("Sending SMS Message asynchronously to %s", newTo));
        message.setCreatedDate(new Date());
        message.setTo(newTo);
        send(message);
    }

    @Transactional
    public SMSResponseDto sendMessage(SMSMessageDto dto) {
        SMSMessage message = smsMessageMapper.smsDtoToSMS(dto);
        Optional<String> parseTo = parseTo(dto.getTo());

        if (parseTo.isEmpty()) {
            final String s = String.format("Invalid msisdn format %s", message.getTo());
            log.error(s);

            return SMSResponseDto.builder()
                    .status(false)
                    .message(s)
                    .build();
        }

        final String newTo = parseTo.get();
        log.info(String.format("Sending SMS Message synchronously to %s", newTo));

        message.setUserId(K.getUserId());
        message.setCreatedDate(new Date());
        message.setSentBy(K.getEmail());
        message.setTo(newTo);

        return send(message);
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

    private SMSResponseDto send(SMSMessage message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        SMSRequest sms = new SMSRequest(message.getMessage(), message.getTo(), apiKey);

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(sms), headers);
            final SMSResponse smsResponse = restTemplate.postForObject(smsUrl, entity, SMSResponse.class);

            if (smsResponse != null) {
                final boolean status = "ok".equals(smsResponse.getCode());
                log.info(String.format("SMS Response is %s", smsResponse.getCode()));

                message.setResponse(smsResponse.getMessage());
                message.setStatus(status);
                final SMSMessage save = smsMessageRepository.save(message);

                return SMSResponseDto.builder()
                        .id(save.getId())
                        .status(status)
                        .message(status ? "Message Sent Successfully" : "Message Send Failed")
                        .build();
            } else {
                message.setStatus(false);
                smsMessageRepository.save(message);
            }

        } catch (JsonProcessingException e) {
            log.error("Error Processing {}", e.getMessage());
        }

        return SMSResponseDto.builder()
                .status(false)
                .message("Message Send Failed")
                .build();
    }

    private Optional<String> parseTo(String to) {
        String newTo = null;

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
            }
        } catch (Exception ex) {
            log.error("Invalid msisdn format exception {}", ex.getMessage());
        }

        return Optional.ofNullable(newTo);
    }

    private PagedDto<SMSMessageDto> createdDtos(Page<SMSMessage> messages) {
        PagedDto<SMSMessageDto> pagedDto = new PagedDto<>();

        final List<SMSMessageDto> collect = messages.toList()
                .stream()
                .map(smsMessageMapper::smsToSMSDto)
                .collect(Collectors.toList());

        pagedDto.setList(collect);
        pagedDto.setPages(messages.getTotalPages());
        pagedDto.setPageNumber(messages.getNumber());
        pagedDto.setPageSize(messages.getSize());
        pagedDto.setTotalSize((int) messages.getTotalElements());

        return pagedDto;
    }
}