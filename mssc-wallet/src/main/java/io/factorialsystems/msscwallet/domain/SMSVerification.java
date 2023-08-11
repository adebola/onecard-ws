package io.factorialsystems.msscwallet.domain;

import lombok.*;

import java.time.OffsetDateTime;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMSVerification {
    private String id;
    private String code;
    private String accountId;
    private OffsetDateTime expiry;
    private String msisdn;
    private Boolean verified;
    private OffsetDateTime verifiedOn;
    private OffsetDateTime createdOn;
}