package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.domain.AccountVerification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountVerificationMapper {
    int save(AccountVerification accountVerification);

    AccountVerification findByUserId(String id);
}
