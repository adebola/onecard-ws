package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.dao.AccountSettingMapper;
import io.factorialsystems.msscwallet.domain.AccountSetting;
import io.factorialsystems.msscwallet.dto.kyc.KycSettingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountSettingService {
    private final AuditService auditService;
    private final AccountSettingMapper accountSettingMapper;

    private static final String AUDIT_ACTION_CHANGE_KYC = "KYC Change";

    private static final Map<Integer, AccountSetting> settingsMap = new ConcurrentHashMap<>();
    private Boolean limitEnabled = null;

    public static final int SETTINGS_DAILY_BALANCE_USER = 1;
    public static final int SETTINGS_DAILY_BALANCE_ORGANIZATION = 2;
    public static final int SETTINGS_DAILY_BALANCE_LIMIT_ENABLED = 3;
    public static final int SETTINGS_FIRST_NAME_COMPARE = 4;
    public static final int SETTINGS_LAST_NAME_COMPARE = 5;
    public static final int SETTINGS_TELEPHONE_COMPARE = 6;

    private void loadSettings() {
        accountSettingMapper.findAll().forEach(a -> {
            settingsMap.put(a.getId(), a);

            if (a.getId() == SETTINGS_DAILY_BALANCE_LIMIT_ENABLED) {
                limitEnabled = a.getValue().equals("1");
            }
        });
    }

    public AccountSetting getAccountSetting(Integer id) {
        if (settingsMap.isEmpty()) {
            loadSettings();
        }

        return settingsMap.get(id);
    }

    public Boolean isLimitEnabled() {
        if (limitEnabled == null) {
            loadSettings();
        }

        return limitEnabled;
    }

    public void changeKycSettings(KycSettingDto dto) {
        boolean reload = false;

        log.info("Change KYC Settings {}", dto);

        if (dto.getEnable() != null) {
            AccountSetting setting = new AccountSetting();
            setting.setId(SETTINGS_DAILY_BALANCE_LIMIT_ENABLED);

            setting.setValue(dto.getEnable());
            accountSettingMapper.update(setting);

            final String value = dto.getEnable().equals("1") ? "true" : "false";
            final String auditMessage = String.format("KYC Enabled changed to %s", value);
            auditService.auditEvent(auditMessage, AUDIT_ACTION_CHANGE_KYC);
            log.info(auditMessage);
            reload = true;
        }

        if (dto.getUserLimit() != null) {
            AccountSetting setting = new AccountSetting();
            setting.setId(SETTINGS_DAILY_BALANCE_USER);
            setting.setValue(String.valueOf(dto.getUserLimit()));

            accountSettingMapper.update(setting);

            final String auditMessage = String.format("KYC User Limit changed to %.2f", dto.getUserLimit());
            auditService.auditEvent(auditMessage, AUDIT_ACTION_CHANGE_KYC);
            log.info(auditMessage);
            reload = true;
        }

        if (dto.getCorporateLimit() != null) {
            AccountSetting setting = new AccountSetting();
            setting.setId(SETTINGS_DAILY_BALANCE_ORGANIZATION);
            setting.setValue(String.valueOf(dto.getCorporateLimit()));

            accountSettingMapper.update(setting);

            final String auditMessage = String.format("KYC Corporate Limit changed to %.2f", dto.getCorporateLimit());
            auditService.auditEvent(auditMessage, AUDIT_ACTION_CHANGE_KYC);
            log.info(auditMessage);
            reload = true;
        }

        if (dto.getFirstName() != null) {
            AccountSetting setting = new AccountSetting();
            setting.setId(SETTINGS_FIRST_NAME_COMPARE);
            setting.setValue(dto.getFirstName());
            accountSettingMapper.update(setting);

            final String value = dto.getFirstName().equals("1") ? "true" : "false";
            final String auditMessage = String.format("KYC Search by First Name changed %s", value);
            auditService.auditEvent(auditMessage, AUDIT_ACTION_CHANGE_KYC);
            log.info(auditMessage);
            reload = true;
        }

        if (dto.getLastName() != null) {
            AccountSetting setting = new AccountSetting();
            setting.setId(SETTINGS_LAST_NAME_COMPARE);
            setting.setValue(dto.getLastName());
            accountSettingMapper.update(setting);

            final String value = dto.getLastName().equals("1") ? "true" : "false";
            final String auditMessage = String.format("KYC Search by Last Name changed %s", value);
            auditService.auditEvent(auditMessage, AUDIT_ACTION_CHANGE_KYC);
            log.info(auditMessage);
            reload = true;
        }

        if (dto.getTelephone() != null) {
            AccountSetting setting = new AccountSetting();
            setting.setId(SETTINGS_TELEPHONE_COMPARE);
            setting.setValue(dto.getTelephone());
            accountSettingMapper.update(setting);

            final String value = dto.getTelephone().equals("1") ? "true" : "false";
            final String auditMessage = String.format("KYC Search by Telephone Number changed %s", value);
            auditService.auditEvent(auditMessage, AUDIT_ACTION_CHANGE_KYC);
            log.info(auditMessage);
            reload = true;
        }

        if (reload) {
            loadSettings();
        }
    }

    public Map<String, String> getKyCSettings() {
        return  accountSettingMapper.findAll()
                .stream()
                .collect(Collectors.toMap(AccountSetting::getShortName, AccountSetting::getValue));
    }
}
