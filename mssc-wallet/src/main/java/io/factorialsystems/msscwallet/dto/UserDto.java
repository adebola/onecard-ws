package io.factorialsystems.msscwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Null(message = "id cannot be set")
    private String id;

    private String email;
    private String username;
    private String lastName;
    private String firstName;

    @Null(message = "token cannot be set")
    private String token;

    @Null(message = "organizationId cannot be set")
    private String organizationId;
}
