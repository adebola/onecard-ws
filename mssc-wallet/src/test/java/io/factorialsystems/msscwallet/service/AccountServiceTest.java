package io.factorialsystems.msscwallet.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscwallet.dao.FundWalletMapper;
import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.exception.ResourceNotFoundException;
import io.factorialsystems.msscwallet.utils.Security;
import lombok.extern.apachecommons.CommonsLog;
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
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@CommonsLog
@SpringBootTest
class AccountServiceTest {
    @Autowired
    AccountService accountService;

    @Autowired
    FundWalletMapper fundWalletMapper;

    final String client_id = "public-client";
    final String realmPassword = "password";
    final String realmUser = "realm-admin";
    final String authUrl = "http://localhost:8080/auth/realms/onecard/protocol/openid-connect/token";

    @Test
    void saveFundWalletRequest() {
        AccountService.saveFundWalletRequest(BigDecimal.valueOf(100), 1, "ade", "adebola", "narrative");
    }

    @Test
    void testFindByPage() {
        PageHelper.startPage(0, 100);
        try (Page<FundWalletRequest> results = fundWalletMapper.findByUserId("e33b6988-e636-44d8-894d-c03c982d8fa5")) {
            log.info(results.getTotal());
        } catch (Exception ex) {
            log.error("Error");
        }
    }
    @Test
    void findAccounts() {
        var accounts = accountService.findAccounts(1, 20);
        assert (accounts != null);
        assert (accounts.getTotalSize() > 0);
    }

    @Test
    void findWrongAccountById() {
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            String id = "wrong-id";

            AccountDto account = accountService.findAccountById(id);
            assert (account != null);
            assertEquals(id, account.getId());

            log.info(account);
        });

        log.info(exception.getMessage());
    }

    @Test
    void findAccountById() {
        final String id = "275745a4-8fb9-46f6-ac80-ff245bc62fcb";

        AccountDto account = accountService.findAccountById(id);
        assertNotNull (account);
        assertEquals(id, account.getId());

        log.info(account);
    }


    @Test
    void findAccountByUserId() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<Security> security  = Mockito.mockStatic(Security.class)) {
            security.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            String userId = "31c2a399-8a45-4cd0-b6da-40c2f295d9d7";
            AccountDto dto = accountService.findAccountByUserId(userId);
            assertNotNull (dto);
        }
    }

    @Test
    @Rollback
    @Transactional
    void fundWallet2() {

        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String accessToken = getUserToken(id);
        final String adminEmail = "admin@gmail.com";
        final String userName = "test-user";

        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
            k.when(Security::getUserId).thenReturn(id);
            assertThat(Security.getUserId()).isEqualTo(id);

            k.when(Security::getAccessToken).thenReturn(accessToken);
            assertThat(Security.getAccessToken()).isEqualTo(accessToken);

            k.when(Security::getEmail).thenReturn(adminEmail);
            assertThat(Security.getEmail()).isEqualTo(adminEmail);

            k.when(Security::getUserName).thenReturn(userName);
            assertThat(Security.getUserName()).isEqualTo(userName);

            final String accountId = "275745a4-8fb9-46f6-ac80-ff245bc62fcb";
            BigDecimal addition = new BigDecimal(1000);

            AccountDto dto = accountService.findAccountById(accountId);
            assertNotNull (dto);
            log.info(String.format("Current Account Balance Before %.2f", dto.getBalance()));

            BalanceDto balanceDto = new BalanceDto(addition, "narrative");
            accountService.fundWallet(accountId, balanceDto);

            AccountDto newDto = accountService.findAccountById(accountId);
            assertEquals(dto.getBalance().add(addition), newDto.getBalance());
            log.info(String.format("New Account Balance Before %.2f", newDto.getBalance()));
        }
    }

    @Test
    void findAccountBalance() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<Security> security  = Mockito.mockStatic(Security.class)) {
            security.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            var balance = accountService.findAccountBalance();
            assertNotNull (balance);
            log.info(balance.getBalance());
        }
    }

    @Test
    void initializeFundWallet() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<Security> security  = Mockito.mockStatic(Security.class)) {
            security.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            FundWalletRequestDto request = new FundWalletRequestDto();
            request.setAmount(new BigDecimal(1500));

//            var response = accountService.initializeFundWallet(request);
//            assertNotNull (response);
//            log.info(response.getAuthorizationUrl());
        }
    }

    @Test
    @Transactional
    @Rollback
    void fundWallet() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final String transactionId = "897dc52c-79ef-4bd6-b5d0-57367ba0c4b0";

        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
            k.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            var response = accountService.fundWallet(transactionId);
            assertNotNull (response);
            log.info(response);
        }
    }

    @Test
    void refundWallet() {
//        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
//        final String accessToken = getUserToken(id);
//
//        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
//            k.when(Security::getUserId).thenReturn(id);
//            assert Objects.equals(Security.getUserId(), id);
//            log.info(Security.getUserId());
//
//            k.when(Security::getAccessToken).thenReturn(accessToken);
//            assertThat(Security.getAccessToken()).isEqualTo(accessToken);
//            log.info(Security.getAccessToken());
//
//            k.when(Security::getEmail).thenReturn("admin@factorialsystems.io");
//            k.when(Security::getUserName).thenReturn("admin");
//
//            BigDecimal addBalance = new BigDecimal(1000);
//
//            BalanceDto currentDto = accountService.findAccountBalance();
//            log.info(String.format("Current Balance %.2f", currentDto.getBalance()));
//
//            RefundRequestDto dto = RefundRequestDto.builder()
//                    .userId(id)
//                    .amount(addBalance)
//                    .build();
//
//            accountService.asyncRefundWallet(id, dto);
//
//            BalanceDto newDto = accountService.findAccountBalance();
//            assertEquals(currentDto.getBalance().add(addBalance), newDto.getBalance());
//        }
    }

    @Test
    @Rollback
    @Transactional
    void chargeAccount() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final int amount = 2000;

        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
            k.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            var balance = accountService.findAccountBalance();
            assertNotNull (balance);
            log.info("Current Balance " + balance);

            WalletRequestDto dto = new WalletRequestDto();
            dto.setAmount(new BigDecimal(amount));
            accountService.chargeAccount(dto);

            var newBalance = accountService.findAccountBalance();
            assertNotNull(newBalance);

            //assertEquals(balance.getBalance().subtract(new BigDecimal(amount)), newBalance.getBalance());
        }
    }

    @Test
    void findWalletFundings() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
            k.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

           var y = accountService.findWalletFunding(Security.getUserId(), 1, 20);
           assertNotNull(y);
           assert(y.getTotalSize() > 0);
           log.info("Size of Array : " + y.getTotalSize());
           log.info(y.getList().get(0));
        }
    }

    @Test
    void findWalletRequestById() {
        final String id = "cd8c443d-7987-4f8e-84fb-7e747bedd643";

        var x = fundWalletMapper.findById(id);
        log.info(x);
    }

    @Test
    void transferFunds() {

//        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
//        final String accessToken = getUserToken(id);
//
//        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
//            k.when(Security::getUserId).thenReturn(id);
//            assert Objects.equals(Security.getUserId(), id);
//            log.info(Security.getUserId());
//
//            k.when(Security::getAccessToken).thenReturn(accessToken);
//            assertThat(Security.getAccessToken()).isEqualTo(accessToken);
//            log.info(Security.getAccessToken());
//
////            k.when(Constants::getEmail).thenReturn(adminEmail);
////            assertThat(Constants.getEmail()).isEqualTo(adminEmail);
////            log.info(adminEmail);
//
//
//            TransferFundsDto dto = new TransferFundsDto();
//            dto.setRecipient("28e05596-9ad0-4187-ac11-fd93fb7701af");
////            dto.setRecipient("91b1d158-01fa-4f9f-9634-23fcfe72f76a");
//            dto.setAmount(new BigDecimal(51));
//
//            accountService.transferFunds(dto);
//        }
    }

    @Test
    void findWalletRequestReport() {
        final String id = "91b1d158-01fa-4f9f-9634-23fcfe72f76a";
        final String accessToken = getUserToken(id);

        WalletReportRequestDto dto = new WalletReportRequestDto();
        dto.setId("28e05596-9ad0-4187-ac11-fd93fb7701af");

        try (MockedStatic<Security> k  = Mockito.mockStatic(Security.class)) {
            k.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            k.when(Security::getAccessToken).thenReturn(accessToken);
            assertThat(Security.getAccessToken()).isEqualTo(accessToken);
            log.info(Security.getAccessToken());

            var y = accountService.findWalletRequestReport(dto);
            log.info(y.get());
            log.info(y.get().size());
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
                restTemplate.exchange (authUrl, HttpMethod.POST, formEntity, TokenResponseDto.class);

        TokenResponseDto token = response.getBody();

        if (token == null || token.getAccess_token() == null || token.getAccess_token().length() < 1) {
            return null;
        }

        return  token.getAccess_token();
    }


}
