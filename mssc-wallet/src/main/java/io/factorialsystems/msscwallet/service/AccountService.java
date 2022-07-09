package io.factorialsystems.msscwallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscwallet.config.JMSConfig;
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
import org.springframework.jms.core.JmsTemplate;
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
    private final JmsTemplate jmsTemplate;
    private final MailService mailService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;
    private final AccountMapper accountMapper;
    private final FundWalletMapper fundWalletMapper;
    private final TransactionMapper transactionMapper;
    private final AccountMapstructMapper accountMapstructMapper;
    private final FundWalletMapstructMapper fundWalletMapstructMapper;

    private static final String ACCOUNT_CHARGED = "Account Charged";
    private static final String ACCOUNT_CREATED = "Account Created";
    private static final String ACCOUNT_WALLET_SELF_FUNDED = "Wallet Self-Funded";
    public static final String  ACCOUNT_WALLET_ADMIN_FUNDED = "Wallet Funded By Admin";
    public static final String ACCOUNT_WALLET_USER_FUNDED = "Wallet Funded By Another User";
    public static final String ACCOUNT_WALLET_USER_DEBITED = "Wallet Debited by Another User";
    private static final String ACCOUNT_BALANCE_FUNDED = "Account Balance Funded";
    private static final String ACCOUNT_BALANCE_UPDATED = "Account Balance Updated";

    public static final int WALLET_SELF_FUNDED = 1;
    public static final int WALLET_ONECARD_FUNDED = 2;
    public static final int WALLET_USER_FUNDED = 3;
    public static final int WALLET_USER_DEBITED = 4;

    public static final String WALLET_SELF_FUNDED_STRING = "Self Funded";
    public static final String WALLET_ONECARD_FUNDED_STRING = "Onecard Funded";
    public static final String WALLET_USER_FUNDED_STRING = "User Funded";
    public static final String WALLET_USER_DEBIT_STRING = "User DebitXXXXXX";

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
        request.setFundType(WALLET_SELF_FUNDED);

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

            saveTransaction(request.getAmount(), account.getId(), ACCOUNT_WALLET_SELF_FUNDED);

            final String auditMessage =
                        String.format("Account (%s / %s) Successfully Funded By %.2f", account.getId(), account.getName(), request.getAmount().doubleValue());
            log.info(auditMessage);
            auditService.auditEvent(auditMessage, ACCOUNT_BALANCE_FUNDED);

            MailMessageDto mailMessageDto = MailMessageDto.builder()
                    .body(String.format("You have Successfully funded your wallet by %.2f New Balance is %.2f", request.getAmount(), newBalance))
                    .to(K.getEmail())
                    .subject("Fund Wallet report")
                    .build();

            pushMailMessage(mailMessageDto);

            return new MessageDto("Wallet Successfully Funded");
        }

        final String errorMessage = String.format("Request (%s) No Payment Made User %s", id, K.getUserName());
        log.error(errorMessage);

        return new MessageDto(errorMessage);
    }

    @Transactional
    public WalletResponseDto transferFunds(TransferFundsDto dto) {
        Account toAccount = Optional.ofNullable(accountMapper.findAccountById(dto.getRecipient()))
                .orElseThrow(() -> new ResourceNotFoundException("To Account", "id", dto.getRecipient()));

        Account fromAccount = Optional.ofNullable(accountMapper.findAccountByUserId(K.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("From Account", "id", dto.getRecipient()));

        if (fromAccount.getBalance().compareTo(dto.getAmount()) > 0) {
            // Load Accounts from User Module
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new RestTemplateInterceptor());

            SimpleUserDto toSimpleDto =
                    Optional.ofNullable(restTemplate.getForObject(baseLocalUrl + "/api/v1/user/simple/" + toAccount.getUserId(), SimpleUserDto.class))
                            .orElseThrow(() -> new ResourceNotFoundException("SimpleUserDto", "id", toAccount.getUserId()));

            SimpleUserDto fromSimpleDto =
                    Optional.ofNullable(restTemplate.getForObject(baseLocalUrl + "/api/v1/user/simple/" + fromAccount.getUserId(), SimpleUserDto.class))
                            .orElseThrow(() -> new ResourceNotFoundException("SimpleUserDto", "id", fromAccount.getUserId()));

            BigDecimal newFromBalance = fromAccount.getBalance().subtract(dto.getAmount());
            BigDecimal newToBalance = toAccount.getBalance().add(dto.getAmount());

            fromAccount.setBalance(newFromBalance);
            toAccount.setBalance(newToBalance);

            accountMapper.changeBalance(fromAccount);
            accountMapper.changeBalance(toAccount);

            final String message = String.format("Funds Transfer from %s to %s in the sum of %.2f", fromSimpleDto.getUserName(), toSimpleDto.getUserName(), dto.getAmount());
            log.info(message);
            auditService.auditEvent(message, ACCOUNT_WALLET_USER_FUNDED);

            saveTransaction(dto.getAmount(), fromAccount.getId(), ACCOUNT_WALLET_USER_DEBITED);
            saveTransaction(dto.getAmount(), toAccount.getId(), ACCOUNT_WALLET_USER_FUNDED);

            saveFundWalletRequest(dto.getAmount(), WALLET_USER_FUNDED, toAccount.getUserId(), String.format("Onecard User %s Funded Wallet", fromSimpleDto.getUserName()));
            saveFundWalletRequest(dto.getAmount(), WALLET_USER_DEBITED, fromAccount.getUserId(), String.format("Funded Onecard User %s", toSimpleDto.getUserName()));

            MailMessageDto toDto = MailMessageDto.builder()
                    .subject("Wallet Funding")
                    .body(String.format("Your account has been funded By %s in the sum of %.2f, your new balance is %.2f", fromSimpleDto.getUserName(),dto.getAmount(), newToBalance))
                    .to(toSimpleDto.getEmail())
                    .build();

            MailMessageDto fromDto = MailMessageDto.builder()
                    .subject("Wallet Funding")
                    .to(fromSimpleDto.getEmail())
                    .body(String.format("You have successfully funded %s by %.2f, your new Balance is %.2f", toSimpleDto.getUserName(), dto.getAmount(), newFromBalance))
                    .build();

            pushMailMessage(toDto);
            pushMailMessage(fromDto);

            return WalletResponseDto.builder()
                    .status(200)
                    .message(message)
                    .build();
        } else {
            final String errorMessage = String.format("Insufficient Funds to transfer %.2f current balance %.2f", dto.getAmount(), fromAccount.getBalance());
            log.error(errorMessage);

            return WalletResponseDto.builder()
                    .status(400)
                    .message(errorMessage)
                    .build();
        }
    }

    @Transactional
    public void fundWallet(String id, BalanceDto dto) {
        Account account = Optional.ofNullable(accountMapper.findAccountById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        BigDecimal newBalance = account.getBalance().add(dto.getBalance());

        account.setBalance(newBalance);
        accountMapper.changeBalance(account);

        String auditMessage =
                String.format("Account (%s / %s) Balance increased by %.2f to %.2f by (%s)",account.getId(), account.getName(), dto.getBalance(), account.getBalance(), K.getUserName());
        log.info(auditMessage);
        auditService.auditEvent(auditMessage, ACCOUNT_BALANCE_UPDATED);
        saveTransaction(dto.getBalance(), account.getId(), ACCOUNT_WALLET_ADMIN_FUNDED);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new RestTemplateInterceptor());
        SimpleUserDto simpleUserDto =
                restTemplate.getForObject(baseLocalUrl + "/api/v1/user/simple/" + account.getUserId(), SimpleUserDto.class);

        if (simpleUserDto == null) {
            log.error(String.format("Unable to retrieve User For Account %s", account.getId()));
            return;
        }

        final String requestId = UUID.randomUUID().toString();

        FundWalletRequest request = FundWalletRequest.builder()
                .id(requestId)
                .amount(dto.getBalance())
                .fundType(WALLET_ONECARD_FUNDED)
                .paymentVerified(true)
                .closed(true)
                .status(200)
                .userId(simpleUserDto.getId())
                .paymentId(requestId)
                .message("Onecard Admin Wallet Funding")
                .build();

        fundWalletMapper.saveClosedAndVerified(request);

        log.info(String.format("Sending Notification of Admin Wallet Update to %s", simpleUserDto.getEmail()));

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .to(simpleUserDto.getEmail())
                .body(String.format("Your account Balance has been funded by Onecard Admin by %.2f Your New Balance is Now %.2f", dto.getBalance(), newBalance))
                .subject("Wallet Funded")
                .build();

        pushMailMessage(mailMessageDto);
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

    public PagedDto<FundWalletRequestDto> findWalletFundings(String userId, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber,pageSize);
        Page<FundWalletRequest> requests = fundWalletMapper.findByUserId(userId);

        return createFundDto(requests);
    }

    public void sendMailMessage(MailMessageDto dto) {
        mailService.sendMailWithOutAttachment(dto);
    }

    private Boolean checkPayment(String id) {
        RestTemplate restTemplate = new RestTemplate();
        PaymentRequestDto dto
                = restTemplate.getForObject(baseLocalUrl + "api/v1/pay/" + id, PaymentRequestDto.class);

        return dto != null ? dto.getVerified() : false;
    }

    private void saveFundWalletRequest(BigDecimal amount, Integer fundType, String userId, String narrative) {
        final String id = UUID.randomUUID().toString();

        FundWalletRequest request = FundWalletRequest.builder()
                .id(id)
                .amount(amount)
                .fundType(fundType)
                .paymentVerified(true)
                .closed(true)
                .status(200)
                .userId(userId)
                .paymentId(id)
                .message(narrative)
                .build();

        fundWalletMapper.saveClosedAndVerified(request);
    }

    private void pushMailMessage(MailMessageDto dto) {
        try {
            jmsTemplate.convertAndSend(JMSConfig.SEND_MAIL_QUEUE, objectMapper.writeValueAsString(dto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private PagedDto<FundWalletRequestDto> createFundDto(Page<FundWalletRequest> requests) {
        PagedDto<FundWalletRequestDto> pagedDto = new PagedDto<>();
        pagedDto.setTotalSize((int) requests.getTotal());
        pagedDto.setPageNumber(requests.getPageNum());
        pagedDto.setPageSize(requests.getPageSize());
        pagedDto.setPages(requests.getPages());
        pagedDto.setList(fundWalletMapstructMapper.listRequestToRequestDto(requests.getResult()));
        return pagedDto;
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

    private void saveTransaction(BigDecimal amount, String accountId, String narrative) {
        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .recipient(accountId)
                .serviceId(100)
                .serviceName(narrative)
                .txAmount(amount)
                .requestId("NOT APPLICABLE")
                .build();

        transactionMapper.save(transaction);
    }
}