package io.factorialsystems.mssccommunication.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("mail")
public class MailMessage {
    @Id
    private String id;
    private String to;
    private String from;
    private String subject;
    private String body;
    private String fileName;
    private Date createdDate;
    private String sentBy;
}
