package io.factorialsystems.msscaudit.repository;

import io.factorialsystems.msscaudit.document.ContactMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactMessageRepository extends MongoRepository<ContactMessage, String> {
    Page<ContactMessage> findByNameStartsWithIgnoreCase(String name, Pageable pageable);
}
