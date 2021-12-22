package io.factorialsystems.msscaudit.mapper;

import io.factorialsystems.msscaudit.document.AuditMessage;
import io.factorialsystems.msscaudit.dto.AuditMessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public interface AuditMessageMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "serviceName", target = "serviceName"),
            @Mapping(source = "serviceAction", target = "serviceAction"),
            @Mapping(source = "userName", target = "userName"),
            @Mapping(source = "createdDate", target = "createdDate"),
            @Mapping(source = "description", target = "description")
    })
    AuditMessageDto auditToAuditDto(AuditMessage message);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "serviceName", target = "serviceName"),
            @Mapping(source = "serviceAction", target = "serviceAction"),
            @Mapping(source = "userName", target = "userName"),
            @Mapping(source = "createdDate", target = "createdDate"),
            @Mapping(source = "description", target = "description")
    })
    AuditMessage auditDtoToAudit(AuditMessageDto dto);
    List<AuditMessageDto> listAuditToAuditDto(List<AuditMessage> messages);
    List<AuditMessage> listAuditDtoToAudit(List<AuditMessageDto> dtos);
}
