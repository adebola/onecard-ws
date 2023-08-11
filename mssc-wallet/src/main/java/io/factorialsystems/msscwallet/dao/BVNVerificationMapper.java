package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.domain.BVNVerification;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BVNVerificationMapper {
    int save(BVNVerification verification);

    int verify(String id);

    BVNVerification findById(String id);

    Boolean checkIfExistsByUserId(String id);

    BVNVerification findByAccountIdVerified(String id);
}