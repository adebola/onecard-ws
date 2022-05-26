package io.factorialsystems.mssccommunication.dto;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class BulkSMSMessageDto {
    List<@Valid SMSMessageDto> messages;
}
