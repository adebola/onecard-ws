package io.factorialsystems.msscprovider.dto.user;

import lombok.Data;

@Data
public class SimpleUserDto {
    private String id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
}
