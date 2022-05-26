package io.factorialsystems.mssccommunication.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("sms")
public class SMSMessage {
    @Id
    private String id;
    private String to;
    private String message;
    private String userId;
    private Date createdDate;
    private String response;
    private boolean status;
}
