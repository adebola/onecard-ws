package io.factorialsystems.mssccommunication.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("mailconstants")
public class MailConstant {
    @Id
    private String id;
    private String name;
    private String value;
}
