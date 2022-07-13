package io.factorialsystems.mssccommunication.dto;

import lombok.Data;

import javax.validation.constraints.Null;
import java.util.Date;

@Data
public class MailMessageDto {
    @Null(message = "Id cannot be set")
    private String id;

    private String to;

    @Null(message = "From is set Internally")
    private String from;

    private String subject;
    private String body;

    @Null(message = "FileName cannot be set")
    private String fileName;

    @Null(message = "Mail sent Date cannot be set")
    private Date createdDate;

    @Null(message = "Mail sentBy cannot be set")
    private String sentBy;
}
