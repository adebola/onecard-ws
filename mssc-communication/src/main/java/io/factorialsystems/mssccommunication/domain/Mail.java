package io.factorialsystems.mssccommunication.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("mail")
public class Mail {
    @Id
    private String id;
    private String to;
    private String from;
    private String subject;
    private String body;
    private String fileName;
}
