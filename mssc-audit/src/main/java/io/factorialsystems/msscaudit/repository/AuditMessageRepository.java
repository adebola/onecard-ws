package io.factorialsystems.msscaudit.repository;

import io.factorialsystems.msscaudit.document.AuditMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditMessageRepository extends MongoRepository<AuditMessage, String> {
}
