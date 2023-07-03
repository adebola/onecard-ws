package io.factorialsystems.msscreports.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuditSearchDto {
    String searchAction;
    String start;
    String end;
}
