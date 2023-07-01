package io.factorialsystems.msscaudit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.time.Instant;

@Value
public class AuditSearchDto {
    String searchAction;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    Instant start;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    Instant end;
}
