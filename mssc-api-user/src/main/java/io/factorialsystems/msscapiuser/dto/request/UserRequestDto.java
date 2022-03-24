package io.factorialsystems.msscapiuser.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequestDto {
    private String client_id;
    private String grant_type;
    private String scope;
    private String subject_token;
}
