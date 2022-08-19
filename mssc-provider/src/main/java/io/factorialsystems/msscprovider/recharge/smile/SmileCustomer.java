package io.factorialsystems.msscprovider.recharge.smile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmileCustomer {
    private String firstName;
    private String lastName;
}
