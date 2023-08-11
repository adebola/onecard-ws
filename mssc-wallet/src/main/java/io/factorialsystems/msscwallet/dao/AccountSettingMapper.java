package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.domain.AccountSetting;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountSettingMapper {
    List<AccountSetting> findAll();
    int create(AccountSetting accountSetting);
    int update(AccountSetting accountSetting);
    AccountSetting findById(Integer id);
}
