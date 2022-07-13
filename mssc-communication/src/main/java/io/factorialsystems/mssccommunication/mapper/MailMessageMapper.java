package io.factorialsystems.mssccommunication.mapper;


import io.factorialsystems.mssccommunication.document.MailMessage;
import io.factorialsystems.mssccommunication.dto.MailMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface MailMessageMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "to", target = "to"),
            @Mapping(source = "from", target = "from"),
            @Mapping(source = "body", target = "body"),
            @Mapping(source = "subject", target = "subject"),
            @Mapping(source = "fileName", target = "fileName"),
            @Mapping(source = "sentBy", target = "sentBy"),
            @Mapping(source = "createdDate", target = "createdDate")
    })
    MailMessageDto mailMessageToDto(MailMessage message);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "to", target = "to"),
            @Mapping(target = "from", ignore = true),
            @Mapping(source = "body", target = "body"),
            @Mapping(source = "subject", target = "subject"),
            @Mapping(target = "fileName", ignore = true),
            @Mapping(target = "sentBy", ignore = true),
            @Mapping(target = "createdDate", ignore = true)
    })
    MailMessage dtoToMailMessageTo(MailMessageDto dto);
}
