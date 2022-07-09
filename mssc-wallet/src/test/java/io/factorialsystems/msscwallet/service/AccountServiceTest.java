package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.dao.FundWalletMapper;
import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.exception.ResourceNotFoundException;
import io.factorialsystems.msscwallet.utils.K;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@CommonsLog
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

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            String userId = "31c2a399-8a45-4cd0-b6da-40c2f295d9d7";
            AccountDto dto = accountService.findAccountByUserId(userId);
            assertNotNull (dto);
        }
    }

    @Test
    void fundWallet2() {

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final String accessToken = getUserToken(id);
        final String adminEmail = "admin@gmail.com";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            k.when(K::getAccessToken).thenReturn(accessToken);
            assertThat(K.getAccessToken()).isEqualTo(accessToken);
            log.info(K.getAccessToken());

            k.when(K::getEmail).thenReturn(adminEmail);
            assertThat(K.getEmail()).isEqualTo(adminEmail);
            log.info(adminEmail);

            final String accountId = "275745a4-8fb9-46f6-ac80-ff245bc62fcb";
            BigDecimal addition = new BigDecimal(1000);

            AccountDto dto = accountService.findAccountById(accountId);
            assertNotNull (dto);
            log.info(String.format("Current Account Balance Before %.2f", dto.getBalance()));

            BalanceDto balanceDto = new BalanceDto(addition);
            accountService.fundWallet(accountId, balanceDto);

            AccountDto newDto = accountService.findAccountById(accountId);
            assertEquals(dto.getBalance().add(addition), newDto.getBalance());
            log.info(String.format("New Account Balance Before %.2f", newDto.getBalance()));
        }
    }

    @Test
    void findAccountBalance() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            var balance = accountService.findAccountBalance();
            assertNotNull (balance);
            log.info(balance.getBalance());
        }
    }

    @Test
    void initializeFundWallet() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            FundWalletRequestDto request = new FundWalletRequestDto();
            request.setAmount(new BigDecimal(1500));

//            var response = accountService.initializeFundWallet(request);
//            assertNotNull (response);
//            log.info(response.getAuthorizationUrl());
        }
    }

    @Test
    void fundWallet() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final String transactionId = "897dc52c-79ef-4bd6-b5d0-57367ba0c4b0";

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            var response = accountService.fundWallet(transactionId);
            assertNotNull (response);
            log.info(response);
        }
    }

    @Test
    void chargeAccount() {
        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final int amount = 200;

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

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

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

           var y = accountService.findWalletFundings(K.getUserId(), 1, 20);
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

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";
        final String accessToken = getUserToken(id);

        try (MockedStatic<K> k  = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assert Objects.equals(K.getUserId(), id);
            log.info(K.getUserId());

            k.when(K::getAccessToken).thenReturn(accessToken);
            assertThat(K.getAccessToken()).isEqualTo(accessToken);
            log.info(K.getAccessToken());

//            k.when(K::getEmail).thenReturn(adminEmail);
//            assertThat(K.getEmail()).isEqualTo(adminEmail);
//            log.info(adminEmail);


            TransferFundsDto dto = new TransferFundsDto();
            dto.setRecipient("275745a4-8fb9-46f6-ac80-ff245bc62fcb");
            dto.setAmount(new BigDecimal(15));

            accountService.transferFunds(dto);
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
