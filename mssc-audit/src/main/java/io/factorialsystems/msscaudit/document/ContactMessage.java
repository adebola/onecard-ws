package io.factorialsystems.msscaudit.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("contactmessages")
public class ContactMessage {
    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private String message;
    private Date createdDate;
}
