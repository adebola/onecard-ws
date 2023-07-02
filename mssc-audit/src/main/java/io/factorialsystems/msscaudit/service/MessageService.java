package io.factorialsystems.msscaudit.service;

import io.factorialsystems.msscaudit.document.AuditMessage;
import io.factorialsystems.msscaudit.dto.AuditMessageDto;
import io.factorialsystems.msscaudit.dto.AuditSearchDto;
import io.factorialsystems.msscaudit.dto.PagedDto;
import io.factorialsystems.msscaudit.mapper.AuditMessageMapper;
import io.factorialsystems.msscaudit.repository.AuditMessageRepository;
import io.factorialsystems.msscaudit.utils.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    private final AuditMessageMapper auditMessageMapper;
    private final AuditMessageRepository auditMessageRepository;

    public PagedDto<AuditMessageDto> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable =
                PageRequestBuilder.buildWithSort(pageNumber, pageSize, "createdDate", true);
        Page<AuditMessage> messages = auditMessageRepository.findAll(pageable);
        return createPagedAuditMessage(messages);
    }

    public PagedDto<AuditMessageDto> search(Integer pageNumber, Integer pageSize, AuditSearchDto auditSearchDto) {
        Pageable pageable = PageRequestBuilder.buildWithSort(pageNumber, pageSize, "createdDate", true);

        if (auditSearchDto != null) {
            if (auditSearchDto.getSearchAction() != null) {
                return createPagedAuditMessage(auditMessageRepository.findPageableByServiceActionLike(auditSearchDto.getSearchAction(), pageable));
            } else if (auditSearchDto.getStart() != null) {
                if (auditSearchDto.getEnd() == null ) {
                    return createPagedAuditMessage(auditMessageRepository.findPageableByCreatedDateBetween(auditSearchDto.getStart(), new Date(), pageable));
                } else {
                    return createPagedAuditMessage(auditMessageRepository.findPageableByCreatedDateBetween(auditSearchDto.getStart(), auditSearchDto.getEnd(), pageable));
                }
            }
        }

        return createPagedAuditMessage(auditMessageRepository.findAll(pageable));
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

    public List<AuditMessageDto> findAllUnPaged(AuditSearchDto dto) {

        List<AuditMessage> messages = null;

        if (dto != null) {
            if (dto.getStart() != null) {
                if (dto.getEnd() == null) {
                    messages = auditMessageRepository.findByCreatedDateBetween(dto.getStart(), new Date(), Sort.by("createdDate").descending());
                } else {
                    messages = auditMessageRepository.findByCreatedDateBetween(dto.getStart(), dto.getEnd(), Sort.by("createdDate").descending());
                }
            }
        }

        if (messages == null) {
            messages = auditMessageRepository.findAll();
        }

        return messages.stream()
                .map(auditMessageMapper::auditToAuditDto)
                .collect(Collectors.toList());
    }

    private PagedDto<AuditMessageDto> createPagedAuditMessage(Page<AuditMessage> messages) {
        PagedDto<AuditMessageDto> pagedDto = new PagedDto<>();

        pagedDto.setList(auditMessageMapper.listAuditToAuditDto(messages.toList()));
        pagedDto.setPages(messages.getTotalPages());
        pagedDto.setPageNumber(messages.getNumber());
        pagedDto.setPageSize(messages.getSize());
        pagedDto.setTotalSize((int) messages.getTotalElements());

        return pagedDto;
    }
}
