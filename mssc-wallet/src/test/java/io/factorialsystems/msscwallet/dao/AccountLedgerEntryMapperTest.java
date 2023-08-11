package io.factorialsystems.msscwallet.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscwallet.domain.AccountLedgerEntry;
import io.factorialsystems.msscwallet.domain.query.AccountLedgerSearch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountLedgerEntryMapperTest {

    @Autowired
    AccountLedgerMapper mapper;

    @Test
    void findAll() {
        final String id1 = UUID.randomUUID().toString();
        final String id2 = UUID.randomUUID().toString();
        final String accountId = "4bdd2394-725f-11ec-890f-51aa61e7643e";

        AccountLedgerEntry entry1 = new AccountLedgerEntry();
        entry1.setAccountId(accountId);
        entry1.setOperation(1);
        entry1.setAmount(new BigDecimal(1200));
        entry1.setId(id1);
        entry1.setDescription("Description");

        mapper.save(entry1);

        AccountLedgerEntry entry2 = new AccountLedgerEntry();
        entry2.setAccountId(accountId);
        entry2.setOperation(1);
        entry2.setAmount(new BigDecimal(1600));
        entry2.setId(id2);
        entry2.setDescription("Description");

        mapper.save(entry2);

        final Page<AccountLedgerEntry> all = mapper.findAll();
        assertThat(all.getResult().size()).isGreaterThan(2);
    }

    @Test
    void findById() {
        save();
    }

    @Test
    void findByAccountId() {
        final String id1 = UUID.randomUUID().toString();
        final String id2 = UUID.randomUUID().toString();
        final String accountId = "4bdd2394-725f-11ec-890f-51aa61e7643e";

        AccountLedgerEntry entry1 = new AccountLedgerEntry();
        entry1.setAccountId(accountId);
        entry1.setOperation(1);
        entry1.setAmount(new BigDecimal(1200));
        entry1.setId(id1);
        entry1.setDescription("Description");

        mapper.save(entry1);

        AccountLedgerEntry entry2 = new AccountLedgerEntry();
        entry2.setAccountId(accountId);
        entry2.setOperation(1);
        entry2.setAmount(new BigDecimal(1600));
        entry2.setId(id2);
        entry2.setDescription("Description");

        mapper.save(entry2);

        List<AccountLedgerEntry> entries = mapper.findByAccountId(accountId);
        assertThat(entries).isNotNull();
        assertThat(entries.size()).isEqualTo(2);
        assertThat(entries.get(0).getAccountId()).isEqualTo(accountId);

        log.info("Entries {}", entries);
    }

    @Test
    void findTotalExpenditureByDay() {
        final String id1 = UUID.randomUUID().toString();
        final String id2 = UUID.randomUUID().toString();
        final String accountId = "4bdd2394-725f-11ec-890f-51aa61e7643e";

        AccountLedgerEntry e1 = new AccountLedgerEntry();
        e1.setAccountId(accountId);
        e1.setOperation(1);
        e1.setAmount(new BigDecimal(1200));
        e1.setId(id1);
        e1.setDescription("Description1");

        mapper.save(e1);

        AccountLedgerEntry e2 = new AccountLedgerEntry();
        e2.setAccountId(accountId);
        e2.setOperation(1);
        e2.setAmount(new BigDecimal(1400));
        e2.setId(id2);
        e2.setDescription("Description2");

        mapper.save(e2);

        AccountLedgerSearch ledgerSearch = new AccountLedgerSearch();
        ledgerSearch.setDate(new Date());
        ledgerSearch.setId(accountId);
        BigDecimal balance = mapper.findTotalExpenditureByDay(ledgerSearch);

        assertThat(balance.compareTo(new BigDecimal(2600))).isEqualTo(0);
        log.info("BALANCE {}", balance);


    }

    @Test
    void save() {
        final String id = UUID.randomUUID().toString();
        final String accountId = "4bdd2394-725f-11ec-890f-51aa61e7643e";

        AccountLedgerEntry entry = new AccountLedgerEntry();
        entry.setAccountId(accountId);
        entry.setOperation(1);
        entry.setAmount(new BigDecimal(1200));
        entry.setId(id);
        entry.setDescription("Description");

        final int save = mapper.save(entry);
        log.info("save {}", save);

        AccountLedgerEntry newEntry = mapper.findById(id);
        assertThat(newEntry).isNotNull();
        assertThat(newEntry.getId()).isEqualTo(id);
        assertThat(newEntry.getAccountId()).isEqualTo(accountId);

        log.info("NewEntry {}", newEntry);
    }
}