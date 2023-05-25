package io.factorialsystems.msscwallet.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import io.factorialsystems.msscwallet.dto.WalletReportRequestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FundWalletMapper {
    FundWalletRequest findById(String id);

    Page<FundWalletRequest> findByUserId(String id);

    void save(FundWalletRequest request);

    void saveClosedAndVerified(FundWalletRequest request);

    void update(FundWalletRequest request);

    List<FundWalletRequest> findByCriteria(WalletReportRequestDto dto);
}
