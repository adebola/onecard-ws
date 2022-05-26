package io.factorialsystems.mssccommunication.mapper;

import io.factorialsystems.mssccommunication.document.SMSMessage;
import io.factorialsystems.mssccommunication.dto.SMSMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public interface SMSMessageMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "to", target = "to"),
            @Mapping(source = "message", target = "message"),
            @Mapping(source = "userId", target = "userId"),
            @Mapping(source = "createdDate", target = "createdDate")
    })
    SMSMessageDto smsToSMSDto(SMSMessage message);

    @Mappings({
            @Mapping(target = "id" , ignore = true),
            @Mapping(source = "to", target = "to"),
            @Mapping(source = "message", target = "message"),
            @Mapping(source = "userId", target = "userId"),
            @Mapping(target = "createdDate", ignore = true),
            @Mapping(target = "response", ignore = true)
    })
    SMSMessage smsDtoToSMS(SMSMessageDto dto);
    List<SMSMessageDto> listSMSToSMSDto(List<SMSMessage> messages);


}
