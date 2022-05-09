package io.factorialsystems.msscusers.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserOrganizationAmendDto {
    private String userId;
    private String organizationId;
}
