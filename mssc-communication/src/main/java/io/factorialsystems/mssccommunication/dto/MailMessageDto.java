package io.factorialsystems.mssccommunication.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;

@Getter
@Setter
public class MailMessageDto {
    @NotEmpty
    private String secret;

    @Null(message = "Id cannot be set")
    private String id;

    @NotNull(message = "Message to must be set")
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
