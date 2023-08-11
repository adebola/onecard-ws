package io.factorialsystems.msscwallet.dao;

import io.factorialsystems.msscwallet.domain.AccountSetting;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountSettingMapperTest {

    @Autowired
    AccountSettingMapper mapper;

    @Test
    void findAll() {
        final List<AccountSetting> all = mapper.findAll();
        assertThat(all.size()).isEqualTo(6);
        log.info("Size {}", all.size());
    }

    @Test
    void create() {
        final String name = "Test";
        final String value = "10000";

        AccountSetting accountSetting = new AccountSetting();
        accountSetting.setId(10);
        accountSetting.setShortName("shortName");
        accountSetting.setName(name);
        accountSetting.setValue(value);

        mapper.create(accountSetting);

        AccountSetting a = mapper.findById(accountSetting.getId());
        assertThat(a).isNotNull();
        assertThat(a.getName()).isEqualTo(name);
        assertThat(a.getValue()).isEqualTo(value);
    }

    @Test
    void update() {
        final String name = "Test1";
        final String value = "Value1";

        AccountSetting a = mapper.findById(1);
        a.setValue(value);
        a.setName(name);

        final int update = mapper.update(a);
        log.info("Update {}", update);

        AccountSetting b = mapper.findById(1);
        assertThat(b).isNotNull();
        assertThat(b.getId()).isEqualTo(1);
        assertThat(b.getValue()).isEqualTo(value);
        assertThat(b.getName()).isEqualTo(name);

        log.info("B IS {}", b);
    }

    @Test
    void findById() {
        AccountSetting accountSetting = mapper.findById(1);
        assertThat(accountSetting).isNotNull();
        assertThat(accountSetting.getId()).isEqualTo(1);
        assertThat(accountSetting.getValue()).isEqualTo("75000");
        log.info("AccountSetting {}", accountSetting);
    }
}