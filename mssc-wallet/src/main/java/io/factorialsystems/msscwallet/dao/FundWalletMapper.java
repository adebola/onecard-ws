package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FundWalletMapper {
    FundWalletRequest findById(String id);
    void save(FundWalletRequest request);
    void update (FundWalletRequest request);
}
