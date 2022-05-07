package io.factorialsystems.msscaudit.service;


import io.factorialsystems.msscaudit.document.ContactMessage;
import io.factorialsystems.msscaudit.dto.ContactMessageDto;
import io.factorialsystems.msscaudit.dto.PagedDto;
import io.factorialsystems.msscaudit.mapper.ContactMessageMapper;
import io.factorialsystems.msscaudit.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactMessageMapper contactMessageMapper;
    private final ContactMessageRepository contactMessageRepository;

    public void saveContactMessage(ContactMessageDto dto) {

        ContactMessage contactMessage = contactMessageMapper.contactDtoToContact(dto);
        contactMessage.setCreatedDate(new Date());
        contactMessageRepository.save(contactMessage);
    }

    public ContactMessageDto findById(String id) {
        return contactMessageMapper.contactToContactDto(contactMessageRepository.findById(id).orElse(new ContactMessage()));
    }

    public PagedDto<ContactMessageDto> findAll(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ContactMessage> messages = contactMessageRepository.findAll(pageable);

        return createPagedDto(messages);
    }

    public PagedDto<ContactMessageDto> search(Integer pageNumber, Integer pageSize, String searchString) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<ContactMessage> messages = contactMessageRepository.findByNameStartsWithIgnoreCase(searchString, pageable);

        return  createPagedDto(messages);
    }

    private PagedDto<ContactMessageDto> createPagedDto(Page<ContactMessage> messages) {
        PagedDto<ContactMessageDto> pagedDto = new PagedDto<>();
        pagedDto.setList(contactMessageMapper.listContactToContactDto(messages.toList()));
        pagedDto.setPages(messages.getTotalPages());
        pagedDto.setPageNumber(messages.getNumber());
        pagedDto.setPageSize(messages.getSize());
        pagedDto.setTotalSize((int) messages.getTotalElements());

        return pagedDto;
    }
}
