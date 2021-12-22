package io.factorialsystems.msscusers.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {
    private Integer id;
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private Timestamp createdOn;
}
