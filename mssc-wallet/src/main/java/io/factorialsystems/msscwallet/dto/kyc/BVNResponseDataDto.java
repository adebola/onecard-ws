package io.factorialsystems.msscwallet.dto.kyc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BVNResponseDataDto {
    private String firstName;
    private String middleName;
    private String lastName;
    private String dateOfBirth;
    private String phoneNumber;
}