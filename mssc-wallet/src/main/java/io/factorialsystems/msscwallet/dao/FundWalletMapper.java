package io.factorialsystems.msscwallet.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FundWalletMapper {
    FundWalletRequest findById(String id);
    Page<FundWalletRequest> findByUserId(String id);
    void save(FundWalletRequest request);
    void saveClosedAndVerified(FundWalletRequest request);
    void update (FundWalletRequest request);
}
