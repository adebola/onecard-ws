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
import io.factorialsystems.msscwallet.exception.ResourceNotFoundException;
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
import java.util.Optional;
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

    private static final String ACCOUNT_CHARGED = "Account Charged";
    private static final String ACCOUNT_CREATED = "Account Created";
    private static final String ACCOUNT_WALLET_FUNDED = "Wallet Funded";
    private static final String ACCOUNT_BALANCE_FUNDED = "Account Balance Funded";
    private static final String ACCOUNT_BALANCE_UPDATED = "Account Balance Updated";

    @Value("${api.host.baseurl}")
    private String baseLocalUrl;

    public PagedDto<AccountDto> findAccounts(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Account> accounts = accountMapper.findAccounts();

        return createDto(accounts);
    }

    public BalanceDto findAccountBalance() {
        Account account = Optional.ofNullable(getActiveUserAccount(K.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("ActiveUserAccountByUserId", "id", K.getUserId()));

        return BalanceDto.builder()
                .balance(account.getBalance())
                .build();
    }

    public AccountDto findAccountById(String id) {
        Account account = Optional.ofNullable(accountMapper.findAccountById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        return accountMapstructMapper.accountToAccountDto(account);
    }

    public AccountDto findAccountByUserId(String userId) {
        Account account = Optional.ofNullable(getActiveUserAccount(K.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("ActiveUserAccountByUserId", "id", userId));

        return accountMapstructMapper.accountToAccountDto(account);
    }

    public AccountDto createAccount(CreateAccountDto dto) {
        String id = UUID.randomUUID().toString();

        Account account = Account.builder()
                .id(id)
                .userId(dto.getUserId())
                .accountType(dto.getAccountType())
                .createdBy(K.getUserName())
                .name(dto.getUserName())
                .activated(true)
                .build();

        accountMapper.save(account);

        final String message = String.format("Account Created %s by %s", dto.getUserName(), K.getUserName());
        log.info(message);
        auditService.auditEvent(message, ACCOUNT_CREATED);

        return accountMapstructMapper.accountToAccountDto(accountMapper.findAccountById(id));
    }

    public FundWalletResponseDto initializeFundWallet(FundWalletRequestDto dto) {
        final String id = UUID.randomUUID().toString();
        final String userId = K.getUserId();

        FundWalletRequest request = fundWalletMapstructMapper.dtoToWalletRequest(dto);
        request.setId(id);
        request.setUserId(userId);

        log.info(String.format("Initializing Wallet Funding id %s, for User %s, amount %.2f", id, userId, request.getAmount()));

        PaymentRequestDto paymentRequest = PaymentRequestDto.builder()
                .amount(request.getAmount())
                .paymentMode("paystack")
                .redirectUrl(request.getRedirectUrl())
                .build();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());

        PaymentRequestDto newDto =
                Optional.ofNullable(restTemplate.postForObject(baseLocalUrl + "api/v1/pay", paymentRequest, PaymentRequestDto.class))
                        .orElseThrow(() -> new RuntimeException("Error Initializing Payment Engine in Wallet Topup"));

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

    @Transactional
    public MessageDto fundWallet(String id) {
        FundWalletRequest request = fundWalletMapper.findById(id);

        if (request == null) {
            log.info("Unable to find FundWalletRequest Sleeping...........");

            try {
                Thread.sleep(500);
                request = Optional.ofNullable(fundWalletMapper.findById(id))
                        .orElseThrow(() -> new ResourceNotFoundException("FundWalletRequest", "id",id ));
            } catch (InterruptedException ie) {
                log.error(ie.getMessage());
                return new MessageDto(String.format("Unknown Error Funding Wallet: %s", id));
            }
        }

        if (request.getClosed()) {
            final String errorMessage = String.format("Request (%s) has been fulfilled", id);
            log.error(errorMessage);
            return new MessageDto(errorMessage);
        }

        if (request.getPaymentId() != null && checkPayment(request.getPaymentId())) {
            final String userId = request.getUserId();

            Account account = Optional.ofNullable(getActiveUserAccount(userId))
                    .orElseThrow(() -> new ResourceNotFoundException("ActiveUserAccountByUserId", "id", userId));

            BigDecimal newBalance = account.getBalance().add(request.getAmount());
            account.setBalance(newBalance);
            accountMapper.changeBalance(account);
            log.info(String.format("Completing Fund Wallet Request %s Added %.2f, New Balance %.2f", id, request.getAmount(), account.getBalance()));

            request.setClosed(true);
            request.setPaymentVerified(true);
            fundWalletMapper.update(request);

            saveTransaction(request, account.getId());

            final String auditMessage =
                        String.format("Account (%s / %s) Successfully Funded By %.2f", account.getId(), account.getName(), request.getAmount().doubleValue());
            log.info(auditMessage);
            auditService.auditEvent(auditMessage, ACCOUNT_BALANCE_FUNDED);

            return new MessageDto("Wallet Successfully Funded");
        }

        throw new RuntimeException(String.format("Request (%s) has no Payment", id));
    }

    @Transactional
    public void updateAccountBalance(String id, BalanceDto dto) {
        Account account = Optional.ofNullable(accountMapper.findAccountById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        account.setBalance(account.getBalance().add(dto.getBalance()));
        accountMapper.changeBalance(account);

        String auditMessage = String.format("Account (%s / %s) Balance increased by %.2f to %.2f by (%s)",account.getId(), account.getName(), dto.getBalance(), account.getBalance(), K.getUserName());
        log.info(auditMessage);
        auditService.auditEvent(auditMessage, ACCOUNT_BALANCE_UPDATED);
    }

    @Transactional
    public WalletResponseDto chargeAccount(WalletRequestDto dto) {
        final String userId = K.getUserId();
        final String message = String.format("Charging %.2f to User %s", dto.getAmount(), userId);

        log.info(message);
        auditService.auditEvent(message, ACCOUNT_CHARGED);

        Account account = Optional.ofNullable(getActiveUserAccount(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Account", "UserId", userId));

        if (account.getBalance().compareTo(dto.getAmount()) >= 0)  {
            BigDecimal newValue = account.getBalance().subtract(dto.getAmount());
            account.setBalance(newValue);
            accountMapper.changeBalance(account);

            final String successMsg = String.format("Updated Balance for Account %s is %.2f", account.getId(), newValue);

            log.info(successMsg);
            auditService.auditEvent(successMsg, ACCOUNT_CHARGED);

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

    private Account getActiveUserAccount(String id) {
        log.info("Getting Active User Account For User : " + id);

        Account account = accountMapper.findAccountByUserId(id);
        if (account == null) return null;

        if (account.getChargeAccountId() == null) {
            return account;
        } else {
            return accountMapper.findAccountById(account.getChargeAccountId());
        }
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
                .serviceName(ACCOUNT_WALLET_FUNDED)
                .txAmount(request.getAmount())
                .requestId("NOT APPLICABLE")
                .build();

        transactionMapper.save(transaction);
    }
}