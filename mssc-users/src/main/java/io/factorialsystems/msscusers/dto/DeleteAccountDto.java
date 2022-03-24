package io.factorialsystems.msscusers.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteAccountDto {
    private String id;
    private String deletedBy;
}
