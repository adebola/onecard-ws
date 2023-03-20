package io.factorialsystems.msscaudit.service;

import io.factorialsystems.msscaudit.document.AuditMessage;
import io.factorialsystems.msscaudit.dto.AuditMessageDto;
import io.factorialsystems.msscaudit.dto.PagedDto;
import io.factorialsystems.msscaudit.mapper.AuditMessageMapper;
import io.factorialsystems.msscaudit.repository.AuditMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final AuditMessageMapper auditMessageMapper;
    private final AuditMessageRepository auditMessageRepository;

    public PagedDto<AuditMessageDto> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<AuditMessage> messages = auditMessageRepository.findAll(pageable);
        PagedDto<AuditMessageDto> pagedDto = new PagedDto<>();

        pagedDto.setList(auditMessageMapper.listAuditToAuditDto(messages.toList()));
        pagedDto.setPages(messages.getTotalPages());
        pagedDto.setPageNumber(messages.getNumber());
        pagedDto.setPageSize(messages.getSize());
        pagedDto.setTotalSize((int) messages.getTotalElements());

        return pagedDto;
    }

    public void save(AuditMessageDto dto) {
        AuditMessage auditMessage = auditMessageMapper.auditDtoToAudit(dto);
        auditMessage.setId(null);
        auditMessageRepository.save(auditMessage);
    }

    public AuditMessageDto findById(String id) {
        Optional<AuditMessage> message = auditMessageRepository.findById(id);
        return message.map(auditMessageMapper::auditToAuditDto).orElse(null);
    }
}
