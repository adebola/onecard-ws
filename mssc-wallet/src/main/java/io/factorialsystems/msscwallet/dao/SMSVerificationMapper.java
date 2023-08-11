package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.domain.SMSVerification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SMSVerificationMapper {
    int save(SMSVerification verificationCode);
    SMSVerification findById(String id);
    int verify(String id);
    Boolean checkVerifiedExistsById(String id);
    SMSVerification findByAccountIdVerified(String id);
    Boolean checkVerifiedExistsByUserId(String id);
}
