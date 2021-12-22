package io.factorialsystems.msscaudit.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("auditmessages")
public class AuditMessage {

    @Id
    private String id;

    private String serviceName;
    private String serviceAction;
    private String userName;
    private String description;
    private Date createdDate;
}
