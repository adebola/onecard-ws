package io.factorialsystems.msscwallet.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.dao.FundWalletMapper;
import io.factorialsystems.msscwallet.dao.TransactionMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import io.factorialsystems.msscwallet.domain.Transaction;
import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.mapper.AccountMapstructMapper;
import io.factorialsystems.msscwallet.mapper.FundWalletMapstructMapper;
import io.factorialsystems.msscwallet.security.RestTemplateInterceptor;
import io.factorialsystems.msscwallet.utils.K;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AuditService auditService;
    private final AccountMapper accountMapper;
    private final FundWalletMapper fundWalletMapper;
    private final TransactionMapper transactionMapper;
    private final AccountMapstructMapper accountMapstructMapper;
    private final FundWalletMapstructMapper fundWalletMapstructMapper;

    private static final String[] AccountType = {"USER", "CORPORATE", "PROVIDER"};
    private static final String ACCOUNT_CREATED = "Account Created";
    private static final String ACCOUNT_UPDATED = "Account Updated";
    private static final String ACCOUNT_BALANCE_UPDATED = "Account Balance Updated";
    private static final String ACCOUNT_BALANCE_FUNDED = "Account Balance Funded";

    @Value("${api.host.baseurl}")
    private String baseLocalUrl;

    public PagedDto<AccountDto> findAccounts(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Account> accounts = accountMapper.findAccounts();

        return createDto(accounts);
    }

    public BalanceDto findAccountBalance() {
        String userId = K.getUserId();

        if (userId == null) {
            throw new RuntimeException("User must be Logged In to retrieve Account Balance");
        }

        Account account = accountMapper.findAccountByUserId(userId);

        if (account == null) {
            throw new RuntimeException(String.format("Unable Load Account for (%s), Account not found please contact support", userId));
        }

        return BalanceDto.builder()
                .balance(account.getBalance())
                .build();
    }

    public AccountDto findAccountById(String id) {
        return accountMapstructMapper.accountToAccountDto(accountMapper.findAccountById(id));
    }

    public AccountDto findAccountByUserId(String userId) {
        return accountMapstructMapper.accountToAccountDto(accountMapper.findAccountByUserId(userId));
    }

    public FundWalletResponseDto initializeFundWallet(FundWalletRequestDto dto) {
        FundWalletRequest request = fundWalletMapstructMapper.dtoToWalletRequest(dto);
        request.setId(UUID.randomUUID().toString());
        request.setUserId(K.getUserId());

        PaymentRequestDto paymentRequest = PaymentRequestDto.builder()
                .amount(request.getAmount())
                .paymentMode("paystack")
                .redirectUrl(request.getRedirectUrl())
                .build();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());

        PaymentRequestDto newDto =
                restTemplate.postForObject(baseLocalUrl + "api/v1/payment", paymentRequest, PaymentRequestDto.class);

        if (newDto != null) {
            request.setPaymentId(newDto.getId());
            request.setAuthorizationUrl(newDto.getAuthorizationUrl());
            request.setMessage(newDto.getMessage());
            request.setStatus(newDto.getStatus());
            fundWalletMapper.save(request);

            return FundWalletResponseDto.builder()
                    .id(request.getId())
                    .authorizationUrl(newDto.getAuthorizationUrl())
                    .redirectUrl(newDto.getRedirectUrl())
                    .build();
        }

        throw new RuntimeException("Error Initializing Payment Engine in Wallet Topup");
    }

    @Transactional
    public MessageDto fundWallet(String id) {
        FundWalletRequest request = fundWalletMapper.findById(id);

        if (request != null) {
            if (request.getClosed()) {
                throw new RuntimeException("Request has been fulfilled");
            }

            if (request.getPaymentId() != null && checkPayment(request.getPaymentId())) {
                Account account = accountMapper.findAccountByUserId(request.getUserId());

                if (account != null) {
                    account.setBalance(account.getBalance().add(request.getAmount()));
                    accountMapper.update(account);

                    request.setClosed(true);
                    request.setPaymentVerified(true);
                    fundWalletMapper.update(request);

                    saveTransaction(request, account.getId());

                    final String auditMessage =
                            String.format("Account (%s / %s) Funded By %.2f",account.getId(), account.getName(), request.getAmount().doubleValue());
                    log.info(auditMessage);
                    auditService.auditEvent(auditMessage, ACCOUNT_BALANCE_FUNDED);

                    return new MessageDto("Wallet Successfully Funded");
                }
            }
        }

        throw new RuntimeException("Wallet Funding Unsuccessful, please ensure payment was successful");
    }

    @Transactional
    public void updateAccountBalance(String id, BalanceDto dto) {

        Account account = accountMapper.findAccountByUserId(id);

        if (account == null) {
            throw new RuntimeException(String.format("Unable Load Account for (%s), Account not found please contact support", id));
        }

        account.setBalance(dto.getBalance());
        accountMapper.update(account);

        String auditMessage = String.format("Account (%s / %s) Balance updated updated to %.2f by (%s)",account.getId(), account.getName(), account.getBalance().doubleValue(), K.getUserName());
        log.info(auditMessage);
        auditService.auditEvent(auditMessage, ACCOUNT_BALANCE_UPDATED);
    }

    @Transactional
    public WalletResponseDto chargeAccount(WalletRequestDto dto) {
        String userId = K.getUserId();

        if (userId == null) {
            throw new RuntimeException("User must be logged to charge Wallet");
        }

        Account account = accountMapper.findAccountByUserId(userId);

        if (account == null) {
            throw new RuntimeException(String.format("Unable to retrieve Account for (%s) for the Database, please contact support", userId));
        }

        if (account.getBalance().compareTo(dto.getAmount()) >= 0)  {
            BigDecimal newValue = account.getBalance().subtract(dto.getAmount());
            account.setBalance(newValue);
            accountMapper.update(account);

            return WalletResponseDto.builder()
                    .message("Successful")
                    .status(200)
                    .build();
        }

        return WalletResponseDto.builder()
                .message("Insufficient Balance")
                .status(300)
                .build();
    }

    private Boolean checkPayment(String id) {
        RestTemplate restTemplate = new RestTemplate();
        PaymentRequestDto dto
                = restTemplate.getForObject(baseLocalUrl + "api/v1/pay/" + id, PaymentRequestDto.class);

        return dto != null ? dto.getVerified() : false;
    }

    private PagedDto<AccountDto> createDto(Page<Account> accounts) {
        PagedDto<AccountDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) accounts.getTotal());
        pagedDto.setPageNumber(accounts.getPageNum());
        pagedDto.setPageSize(accounts.getPageSize());
        pagedDto.setPages(accounts.getPages());
        pagedDto.setList(accountMapstructMapper.listAccountToAccountDto(accounts.getResult()));
        return pagedDto;
    }

    private void saveTransaction(FundWalletRequest request, String accountId) {

        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .recipient(accountId)
                .serviceId(100)
                .serviceName("Wallet Funded")
                .txAmount(request.getAmount())
                .requestId("NOT APPLICABLE")
                .build();

        transactionMapper.save(transaction);
    }
}

//    private Account createUserAccount(String userId) {
//        return createAccount(userId, 1);
//    }
//    private Account createCorporateAccount(String userId) { return createAccount(userId,2); }
//    private Account createProviderAccount(String providerId) {
//        return createAccount(providerId, 3);
//    }

//    private Account createAccount(String id, Integer accountType) {
//
//        Account account = Account.builder()
//                .accountType(accountType)
//                .userId(id)
//                .createdBy(K.getUserName())
//                .id(String.valueOf(UUID.randomUUID()))
//                .build();
//
//        accountMapper.save(account);
//
//        String auditMessage = String.format("Account of Type %s with Id %s created", AccountType[accountType - 1], account.getId());
//        auditService.auditEvent(auditMessage, ACCOUNT_CREATED);
//
//        return accountMapper.findAccountById(account.getId());
//    }

//    public AccountDto findAccountByCorporateId(String userId) {
//        return accountMapstructMapper.accountToAccountDto(accountMapper.findAccountByCorporateId(userId));
//    }
//
//    public AccountDto findAccountByProviderId(String providerId) {
//        return  accountMapstructMapper.accountToAccountDto(accountMapper.findAccountByProviderId(providerId));
//    }
//
//
//    public void updateAccount(String id, AccountDto accountDto) {
//
//        if (id != null && accountDto != null) {
//            accountDto.setId(id);
//            accountMapper.update(accountMapstructMapper.accountDtoToAccount(accountDto));
//
//            String auditMessage = String.format("Account %s updated", accountDto.getId());
//            auditService.auditEvent(auditMessage, ACCOUNT_UPDATED);
//        }
//    }
