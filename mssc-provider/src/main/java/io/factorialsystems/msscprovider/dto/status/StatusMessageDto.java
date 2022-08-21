package io.factorialsystems.msscprovider.dto.status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatusMessageDto {
    private Integer status;
    private String message;
}