package io.factorialsystems.mssccommunication.repository;

import io.factorialsystems.mssccommunication.document.SMSMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SMSMessageRepository extends MongoRepository<SMSMessage, String> {
    Page<SMSMessage> findByUserId(Pageable pageable, String userId);
}
