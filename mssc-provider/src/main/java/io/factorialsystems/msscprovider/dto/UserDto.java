package io.factorialsystems.msscprovider.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}