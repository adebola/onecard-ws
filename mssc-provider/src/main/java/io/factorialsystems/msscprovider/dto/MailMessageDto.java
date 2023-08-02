package io.factorialsystems.msscprovider.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class MailMessageDto {
    private String id;
    private String secret;
    private String to;
    private String from;
    private String subject;
    private String body;
    private String fileName;
    private Date createdDate;
    private String sentBy;
}
