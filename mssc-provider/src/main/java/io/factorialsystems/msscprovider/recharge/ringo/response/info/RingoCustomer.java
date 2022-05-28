package io.factorialsystems.msscprovider.recharge.ringo.response.info;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RingoCustomer {
    private Integer id;
    private String name;
    private String email;
    private String email_verified;
    private String firstname;
    private String lastname;
    private String phone;
}
