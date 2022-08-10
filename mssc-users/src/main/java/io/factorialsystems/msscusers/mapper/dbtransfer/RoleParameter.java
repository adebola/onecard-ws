package io.factorialsystems.msscusers.mapper.dbtransfer;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoleParameter {
    private String roleId;
    private String userId;
}
