package io.factorialsystems.msscusers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Null;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakUserDto {

    @Null(message = "id cannot be set")
    private String id;

    @Null(message = "createdDate cannot be set")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private Date createdDate;

    private String username;

    private Boolean enabled;

    @Null(message = "emailVerified cannot be set")
    private Boolean emailVerified;

    private String firstName;
    private String lastName;
    private String email;

    @Null(message = "requiredActions cannot be set")
    private String[] requiredActions;

    private AccountDto account;
}
