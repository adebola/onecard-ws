package io.factorialsystems.msscusers.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class MailMessageDto {
    private String id;
    private String to;
    private String from;
    private String subject;
    private String body;
    private String fileName;
    private Date createdDate;
    private String sentBy;
}
