package io.factorialsystems.mssccommunication.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
public class SMSMessageDto {

    @Null(message = "Id cannot be set")
    private String id;

    @NotEmpty
    private String message;

    @NotEmpty
    private String to;

    @Null(message = "UserId cannot be set")
    private String userId;

    @Null(message = "Date cannot be set")
    private Date createdDate;

    @Null(message = "Response cannot be set")
    private String response;
}
