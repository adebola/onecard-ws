package io.factorialsystems.msscwallet.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class AccountVerification {
    private Long id;
    private String accountId;
    private String smsVerificationId;
    private String bvnVerificationId;
    private String verifiedBy;
    private OffsetDateTime verifiedOn;
}
