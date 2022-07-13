package io.factorialsystems.mssccommunication.repository;

import io.factorialsystems.mssccommunication.document.MailMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MailMessageRepository extends MongoRepository<MailMessage, String> {
}
