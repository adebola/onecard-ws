package io.factorialsystems.msscwallet.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class BVNVerification {
    private String id;
    private String bvn;
    private String status;
    private String accountId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneNumber;
    private String dateOfBirth;
    private Boolean verified;
    private OffsetDateTime verifiedOn;
    private OffsetDateTime createdOn;
}