package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.domain.AccountSetting;
import io.factorialsystems.msscwallet.dto.kyc.KycSettingDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@CommonsLog
@SpringBootTest
class AccountSettingServiceTest {

    @Autowired
    AccountSettingService settingService;

    @Test
    void getAccountSetting() {
        final AccountSetting accountSetting = settingService.getAccountSetting(1);
        assertThat(accountSetting.getValue()).isEqualTo("75000");
        log.info(accountSetting);

        final AccountSetting accountSetting1 = settingService.getAccountSetting(2);
        assertThat(accountSetting1.getValue()).isEqualTo("100001");
        log.info(accountSetting1);

        final AccountSetting accountSetting2 = settingService.getAccountSetting(3);
        assertThat(accountSetting2.getValue()).isEqualTo("1");
        log.info(accountSetting2);
    }

    @Test
    void isLimitEnabled() {
        final Boolean limitEnabled = settingService.isLimitEnabled();
        assertThat(limitEnabled).isEqualTo(true);
    }

    @Test
    void changeKycSettings_dailyLimit() {
        KycSettingDto dto = new KycSettingDto();
        dto.setUserLimit(new BigDecimal(75000));
        settingService.changeKycSettings(dto);

        final AccountSetting accountSetting = settingService.getAccountSetting(1);
        assertThat(accountSetting.getValue()).isEqualTo("75000");
    }

    @Test
    void changeKycSettings_enableLimit() {
        KycSettingDto dto = new KycSettingDto();
        dto.setEnable("1");
        settingService.changeKycSettings(dto);

        final AccountSetting accountSetting = settingService.getAccountSetting(3);
        assertThat(accountSetting.getValue()).isEqualTo("1");
    }
}