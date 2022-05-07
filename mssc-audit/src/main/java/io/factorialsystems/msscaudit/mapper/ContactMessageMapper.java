package io.factorialsystems.msscaudit.mapper;

import io.factorialsystems.msscaudit.document.ContactMessage;
import io.factorialsystems.msscaudit.dto.ContactMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;


@Mapper
public interface ContactMessageMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "phone", target = "phone"),
            @Mapping(source = "message", target = "message"),
            @Mapping(source = "createdDate", target = "createdDate")
    })
    ContactMessageDto contactToContactDto(ContactMessage message);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "phone", target = "phone"),
            @Mapping(source = "message", target = "message"),
            @Mapping(target = "createdDate", ignore = true)
    })
    ContactMessage contactDtoToContact(ContactMessageDto dto);
    List<ContactMessageDto> listContactToContactDto(List<ContactMessage> messages);
    List<ContactMessage> listContactDtoToContact(List<ContactMessageDto> dtos);

}
