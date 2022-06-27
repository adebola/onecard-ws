package io.factorialsystems.msscprovider.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusMessageDto {
    private Integer status;
    private String message;
}