package io.factorialsystems.mssccommunication.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SMSResponseDto {
    private String id;
    private Boolean status;
    private String message;
}
