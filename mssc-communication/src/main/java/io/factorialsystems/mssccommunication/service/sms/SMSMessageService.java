package io.factorialsystems.mssccommunication.service.sms;

import io.factorialsystems.mssccommunication.document.SMSMessage;
import io.factorialsystems.mssccommunication.dto.PagedDto;
import io.factorialsystems.mssccommunication.dto.SMSMessageDto;
import io.factorialsystems.mssccommunication.mapper.SMSMessageMapper;
import io.factorialsystems.mssccommunication.repository.SMSMessageRepository;
import io.factorialsystems.mssccommunication.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

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

    public void sendBulkMessages(List<SMSMessageDto> messages) {
        messages.forEach(this::sendMessage);
    }

    public boolean sendMessage(SMSMessageDto dto) {

        SMSMessage message = smsMessageMapper.smsDtoToSMS(dto);

        RestTemplate restTemplate = new RestTemplate();

        String newTo = null;
        final String to = message.getTo();;

        try {
            if (to.indexOf('0') == 0) {
                newTo = "234" + to.substring(1);
            } else if (to.indexOf("+") == 0) {
                newTo = to.substring(1);
            } else if (to.substring(0,3).compareTo("234") == 0) {
                newTo = to;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }

        if (newTo == null) {
            log.error(String.format("Unable to parse Telephone Number %s", to));
            return false;
        }

        log.info(String.format("Sending Message to %s", newTo));

        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("user", smsUser);
        param.add("pass", smsPassword);
        param.add("from", SMS_TAG);
        param.add("to", newTo);
        param.add("msg", dto.getMessage());
        param.add("type", 0);

        message.setUserId(K.getUserId());
        message.setCreatedDate(new Date());

        try {
            String response = restTemplate.postForObject(smsUrl, param, String.class);
            message.setResponse(response);
            message.setStatus("sent".equals(response));
            log.info(String.format("SMS Response is %s", response));
        } catch (Exception ex) {
            log.error(String.format("Error sending sms to %s Reason: %s", message.getTo(), ex.getMessage()));
            message.setStatus(false);
        }

        smsMessageRepository.save(message);

        return true;
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

