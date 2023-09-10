package io.factorialsystems.mssccommunication.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import java.util.Date;

@Getter
@Setter
@ToString
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
