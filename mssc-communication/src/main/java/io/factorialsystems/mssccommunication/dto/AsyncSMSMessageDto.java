package io.factorialsystems.mssccommunication.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class AsyncSMSMessageDto {
    @NotEmpty(message = "SMS Message cannot be empty")
    private String message;

    @NotEmpty(message = "SMS Message to cannot be empty")
    private String to;

    @NotEmpty(message = "UserId must be specified for async SMS")
    private String userId;

    @NotEmpty(message = "Email must be specified for async SMS")
    private String email;
}
