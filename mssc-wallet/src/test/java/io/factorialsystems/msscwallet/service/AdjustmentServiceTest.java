package io.factorialsystems.msscwallet.service;

import com.github.pagehelper.Page;
import io.factorialsystems.msscwallet.dao.AccountLedgerMapper;
import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.dao.AdjustmentMapper;
import io.factorialsystems.msscwallet.dao.FundWalletMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.domain.AccountLedgerEntry;
import io.factorialsystems.msscwallet.domain.Adjustment;
import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import io.factorialsystems.msscwallet.dto.AdjustmentRequestDto;
import io.factorialsystems.msscwallet.dto.AdjustmentResponseDto;
import io.factorialsystems.msscwallet.utils.Security;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class AdjustmentServiceTest {
    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Autowired
    private AdjustmentService service;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private FundWalletMapper fundWalletMapper;

    @Autowired
    private AdjustmentMapper adjustmentMapper;

    @Autowired
    private AccountLedgerMapper accountLedgerMapper;

    @Test
    @Transactional
    @Rollback
    public void adjustBalance() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String accountId = "275745a4-8fb9-46f6-ac80-ff245bc62fcb";
        final String userName = "debug_test";
        final String accessToken = getUserToken(id);
        final BigDecimal modify = new BigDecimal(10);

        try (MockedStatic<Security> security = Mockito.mockStatic(Security.class)) {
            security.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);

            security.when(Security::getUserName).thenReturn(userName);
            assert Objects.equals(Security.getUserName(), userName);

            security.when(Security::getAccessToken).thenReturn(accessToken);
            assertThat(Security.getAccessToken()).isEqualTo(accessToken);
            log.info(Security.getAccessToken());

            Account account = accountMapper.findAccountById(accountId);
            final BigDecimal oldBalance = account.getBalance();

            log.info("Old Balance {}", oldBalance);

            final String narrative = UUID.randomUUID().toString();

            AdjustmentRequestDto dto = AdjustmentRequestDto.builder()
                    .accountId(accountId)
                    .narrative(narrative)
                    .amount(modify)
                    .build();

            AdjustmentResponseDto responseDto = service.adjustBalance(dto);

            // Test Account Make sure Balance is updated accordingly
            account = accountMapper.findAccountById(accountId);
            final BigDecimal newBalance = account.getBalance();
            log.info("New Balance {}", newBalance);
            assertEquals(oldBalance.subtract(modify).compareTo(newBalance), 0);

            // Test FundWalletRequest Entry
            final Page<FundWalletRequest> requests = fundWalletMapper.findByUserId(id);
            Optional<FundWalletRequest> request = requests
                    .stream()
                    .filter(f -> f.getMessage().equals(narrative))
                    .findFirst();

            assertTrue(request.isPresent());
            BigDecimal fundAmount = request.get().getAmount();
            log.info("Fund Amount {}, Modified By {}", fundAmount, modify);
            assertEquals(fundAmount.multiply(new BigDecimal(-1)).compareTo(modify), 0);
            log.info("FundWalletRequest {}", request.get());

            // Test Adjustment Entry
            final Optional<Adjustment> adjustment = adjustmentMapper.findAll()
                    .stream()
                    .filter(a -> a.getNarrative().equals(narrative))
                    .findFirst();

            assertTrue(adjustment.isPresent());
            assertEquals(adjustment.get().getAdjustedBy(), id);
            assertEquals(adjustment.get().getPreviousValue().compareTo(oldBalance), 0);
            assertEquals(adjustment.get().getAccountId(), accountId);
            assertEquals(adjustment.get().getAdjustedValue().compareTo(modify), 0);

            // Test AccountLedger Entry
            final List<AccountLedgerEntry> entries = accountLedgerMapper.findByAccountId(accountId)
                    .stream()
                    .filter(e -> e.getOperation() == AccountService.LEDGER_OPERATION_SYSTEM_DEBIT)
                    .filter(e -> e.getAmount().compareTo(modify) == 0)
                    .collect(Collectors.toList());

            assertFalse(entries.isEmpty());

            log.info("Entries: {}", entries);
            log.info("Response {}", responseDto);
        }
    }

    private String getRealmAdminToken() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", client_id);
        requestBody.add("grant_type", "password");
        requestBody.add("password", realmPassword);
        requestBody.add("username", realmUser);
        requestBody.add("scope", "openid");

        // Get the Realm Administrator Token
        return getToken(requestBody);
    }

    private String getUserToken(String userId) {

        String realmToken = getRealmAdminToken();

        if (realmToken == null) {
            return null;
        }

        // Now Get the User Token
        return getUserToken(userId, realmToken);
    }

    private String getUserToken(String userId, String realmToken) {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("client_id", client_id);
        requestBody.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        requestBody.add("subject_token", realmToken);
        requestBody.add("requested_subject", userId);

        return getToken(requestBody);
    }

    private String getToken(MultiValueMap<String, String> requestBody) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<TokenResponseDto> response =
                restTemplate.exchange(authUrl, HttpMethod.POST, formEntity, TokenResponseDto.class);

        TokenResponseDto token = response.getBody();

        if (token == null || token.getAccess_token() == null || token.getAccess_token().isEmpty()) {
            return null;
        }

        return token.getAccess_token();
    }

    @Test
    void testAdjustBalance() {
    }
}