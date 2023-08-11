package io.factorialsystems.msscwallet.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountSetting {
    private Integer id;
    private String shortName;
    private String name;
    private String value;
}
