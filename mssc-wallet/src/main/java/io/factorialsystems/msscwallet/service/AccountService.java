package io.factorialsystems.msscwallet.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.factorialsystems.msscwallet.config.ApplicationContextProvider;
import io.factorialsystems.msscwallet.config.JMSConfig;
import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.dao.FundWalletMapper;
import io.factorialsystems.msscwallet.dao.TransactionMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.domain.FundWalletRequest;
import io.factorialsystems.msscwallet.domain.Transaction;
import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.exception.ResourceNotFoundException;
import io.factorialsystems.msscwallet.external.client.PaymentClient;
import io.factorialsystems.msscwallet.external.client.UserClient;
import io.factorialsystems.msscwallet.mapper.AccountMapstructMapper;
import io.factorialsystems.msscwallet.mapper.FundWalletMapstructMapper;
import io.factorialsystems.msscwallet.utils.Constants;
import io.factorialsystems.msscwallet.utils.Security;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserClient userClient;
    private final JmsTemplate jmsTemplate;
    private final MailService mailService;
    private final AuditService auditService;
    private final ObjectMapper objectMapper;
    private final PaymentClient paymentClient;
    private final AccountMapper accountMapper;
    private final FundWalletMapper fundWalletMapper;
    private final AccountMapstructMapper accountMapstructMapper;
    private final FundWalletMapstructMapper fundWalletMapstructMapper;

    public PagedDto<AccountDto> findAccounts(Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<Account> accounts = accountMapper.findAccounts();

        return createDto(accounts);
    }

    public BalanceDto findAccountBalance() {
        Account account = Optional.ofNullable(getActiveUserAccount(Security.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("ActiveUserAccountByUserId", "id", Security.getUserId()));

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
        Account account = Optional.ofNullable(getActiveUserAccount(Security.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("ActiveUserAccountByUserId", "id", userId));

        return accountMapstructMapper.accountToAccountDto(account);
    }

    public AccountDto createAccount(CreateAccountDto dto) {
        String id = UUID.randomUUID().toString();

        Account account = Account.builder()
                .id(id)
                .userId(dto.getUserId())
                .accountType(dto.getAccountType())
                .createdBy(Security.getUserName())
                .name(dto.getUserName())
                .activated(true)
                .build();

        accountMapper.save(account);

        final String message = String.format("Account Created %s by %s", dto.getUserName(), Security.getUserName());
        log.info(message);
        auditService.auditEvent(message, Constants.ACCOUNT_CREATED);

        return accountMapstructMapper.accountToAccountDto(accountMapper.findAccountById(id));
    }

    public FundWalletResponseDto initializeFundWallet(FundWalletRequestDto dto) {
        final String id = UUID.randomUUID().toString();
        final String userId = Security.getUserId();

        FundWalletRequest request = fundWalletMapstructMapper.dtoToWalletRequest(dto);
        request.setId(id);
        request.setActionedBy(Security.getUserName());
        request.setUserId(userId);
        request.setFundType(Constants.WALLET_SELF_FUNDED);

        log.info(String.format("Initializing Wallet Funding id %s, for User %s, amount %.2f", id, userId, request.getAmount()));

        PaymentRequestDto paymentRequest = PaymentRequestDto.builder()
                .amount(request.getAmount())
                .paymentMode("paystack")
                .redirectUrl(request.getRedirectUrl())
                .build();

        PaymentRequestDto newDto = null;

        if (Security.getUserId() == null) {
            newDto = paymentClient.initializePayment(paymentRequest);
        } else {
            newDto = paymentClient.makePayment(paymentRequest);
        }

        if (newDto == null) {
            throw new RuntimeException("Error Initializing Payment Engine in Wallet Topup");
        }

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

            saveTransaction(request.getAmount(), account.getId(), Constants.ACCOUNT_WALLET_SELF_FUNDED);
            saveFundWalletRequest(request.getAmount(), Constants.WALLET_SELF_FUNDED, request.getUserId(), account.getName(), "User Self Funded Wallet");

            final String auditMessage =
                        String.format("Account (%s / %s) Successfully Self-Funded By %.2f", account.getId(), account.getName(), request.getAmount().doubleValue());
            log.info(auditMessage);
            auditService.auditEvent(auditMessage, Constants.ACCOUNT_BALANCE_FUNDED);

            final String message = String.format("Dear %s, You have Successfully funded your wallet by %.2f, Your new Balance is %.2f",
                    Security.getUserName(), request.getAmount(), newBalance);

            MailMessageDto mailMessageDto = MailMessageDto.builder()
                    .body(message)
                    .to(Security.getEmail())
                    .subject("Fund Wallet report")
                    .build();

            mailService.pushMailMessage(mailMessageDto);

            return new MessageDto("Wallet Successfully Funded");
        }

        final String errorMessage = String.format("Request (%s) No Payment Made User %s", id, Security.getUserName());
        log.error(errorMessage);

        return new MessageDto(errorMessage);
    }

    @Transactional
    public WalletResponseDto transferFunds(TransferFundsDto dto) {
        Account toAccount = Optional.ofNullable(accountMapper.findAccountByUserIdOrUserName(dto.getRecipient()))
                .orElseThrow(() -> new ResourceNotFoundException("To Account", "id", dto.getRecipient()));

        Account fromAccount = Optional.ofNullable(accountMapper.findAccountByUserId(Security.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("From Account", "id", Security.getUserId()));

        if (fromAccount.getBalance().compareTo(dto.getAmount()) > 0) {
            // Load Accounts from User Module

            SimpleUserDto toSimpleDto = userClient.getUserById(toAccount.getUserId());

            if (toSimpleDto == null) {
                throw new ResourceNotFoundException("SimpleUserDto", "id", toAccount.getUserId());
            }

            SimpleUserDto fromSimpleDto = userClient.getUserById(fromAccount.getUserId());

            if (fromSimpleDto == null) {
                throw new ResourceNotFoundException("SimpleUserDto", "id", fromAccount.getUserId());
            }

            BigDecimal newFromBalance = fromAccount.getBalance().subtract(dto.getAmount());
            BigDecimal newToBalance = toAccount.getBalance().add(dto.getAmount());

            fromAccount.setBalance(newFromBalance);
            toAccount.setBalance(newToBalance);

            accountMapper.changeBalance(fromAccount);
            accountMapper.changeBalance(toAccount);

            final String message = String.format("Funds Transfer from %s to %s in the sum of %.2f", fromSimpleDto.getUserName(), toSimpleDto.getUserName(), dto.getAmount());
            log.info(message);
            auditService.auditEvent(message, Constants.ACCOUNT_WALLET_USER_FUNDED);

            saveTransaction(dto.getAmount(), fromAccount.getId(), Constants.ACCOUNT_WALLET_USER_DEBITED);
            saveTransaction(dto.getAmount(), toAccount.getId(), Constants.ACCOUNT_WALLET_USER_FUNDED);

            saveFundWalletRequest(dto.getAmount(), Constants.WALLET_USER_FUNDED, toAccount.getUserId(),
                    fromAccount.getName(), String.format("Onecard User %s Funded Wallet", fromSimpleDto.getUserName()));
            saveFundWalletRequest(dto.getAmount(), Constants.WALLET_USER_DEBITED, fromAccount.getUserId(),
                    fromAccount.getName(), String.format("Funded Onecard User %s", toSimpleDto.getUserName()));

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

            mailService.pushMailMessage(toDto);
            mailService.pushMailMessage(fromDto);

            return WalletResponseDto.builder()
                    .status(200)
                    .balance(newFromBalance)
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

    // Refund Wallet request sent via JMS for Automatic Refunds when a Recharge Fails and the Money
    // already been taken
    @SneakyThrows
    @Transactional
    public void asyncRefundWallet(AsyncRefundRequestDto request) {
        Account account = Optional.ofNullable(accountMapper.findAccountByUserId(request.getUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", request.getUserId()));

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        accountMapper.changeBalance(account);

        String auditMessage =
                String.format("Account (%s / %s) Refunded by %.2f to %.2f by (%s)", account.getId(), account.getName(), request.getAmount(), account.getBalance(), Security.getUserName());
        log.info(auditMessage);
        auditService.auditEvent(auditMessage, Constants.ACCOUNT_BALANCE_REFUNDED);

        saveTransaction(request.getAmount(), account.getId(), Constants.ACCOUNT_WALLET_ADMIN_REFUNDED);
        final String refundWalletId = saveFundWalletRequest(request.getAmount(), Constants.WALLET_ONECARD_REFUNDED, request.getUserId(),
                "auto-refund", String.format("Wallet Refunded By %s", request.getUserId()));

        AsyncRefundResponseDto dto = AsyncRefundResponseDto.builder()
                .status(200)
                .amount(request.getAmount())
                .message("success")
                .id(refundWalletId)
                .paymentId(request.getPaymentId())
                .userId(request.getUserId())
                .build();

        if (request.getBulkRechargeId() != null) {
            dto.setBulkRechargeId(request.getBulkRechargeId());
            dto.setIndividualRechargeId(request.getIndividualRechargeId());
        } else {
            dto.setRechargeId(request.getSingleRechargeId());
        }

        final String buffer = objectMapper.writeValueAsString(dto);

        jmsTemplate.convertAndSend(JMSConfig.WALLET_REFUND_RESPONSE_QUEUE_USER, buffer);
        jmsTemplate.convertAndSend(JMSConfig.WALLET_REFUND_RESPONSE_QUEUE_PAYMENT, buffer);
        jmsTemplate.convertAndSend(JMSConfig.WALLET_REFUND_RESPONSE_QUEUE_PROVIDER, buffer);

        if (account.getWebHook() != null) invokeRefundNotification(account, request.getAmount());
    }

    // Admin initiated manual Wallet refund request
    @Transactional
    public RefundResponseDto refundWallet(String id, RefundRequestDto dto) {
        Account account = Optional.ofNullable(accountMapper.findAccountByUserId(id))
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        BigDecimal newBalance = account.getBalance().add(dto.getAmount());

        account.setBalance(newBalance);
        accountMapper.changeBalance(account);

        String auditMessage =
                String.format("Account (%s / %s) Refunded by %.2f to %.2f by (%s)", account.getId(), account.getName(), dto.getAmount(), account.getBalance(), Security.getUserName());
        log.info(auditMessage);
        auditService.auditEvent(auditMessage, Constants.ACCOUNT_BALANCE_REFUNDED);

        saveTransaction(dto.getAmount(), account.getId(), Constants.ACCOUNT_WALLET_ADMIN_REFUNDED);
        final String refundWalletId = saveFundWalletRequest(dto.getAmount(), Constants.WALLET_ONECARD_REFUNDED, id,
                Security.getUserName(), String.format("Wallet Refunded By %s", Security.getUserName()));

        SimpleUserDto simpleUserDto = userClient.getUserById(id);

        if (simpleUserDto == null) {
            throw new ResourceNotFoundException("SimpleUserDto", "id", id);
        }

        final String message = String.format("Dear %s %s\n\nYour account has been refunded with %.2f by Onecard Admin. Your new balance is %.2f",
                simpleUserDto.getFirstName(), simpleUserDto.getLastName(), dto.getAmount(), newBalance);

        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .subject("Wallet Refund")
                .body(message)
                .to(simpleUserDto.getEmail())
                .build();

        mailService.pushMailMessage(mailMessageDto);

        if (account.getWebHook() != null) invokeRefundNotification(account, dto.getAmount());

        return RefundResponseDto.builder()
                .status(200)
                .message("Success")
                .id(refundWalletId)
                .build();
    }

    // Called by Admin to Fund Wallet
    @Transactional
    public NewBalanceDto fundWallet(String id, BalanceDto dto) {
        Account account = Optional.ofNullable(accountMapper.findAccountById(id))
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));

        BigDecimal newBalance = account.getBalance().add(dto.getBalance());

        account.setBalance(newBalance);
        accountMapper.changeBalance(account);

        SimpleUserDto simpleUserDto = null;

        if (account.getAccountType() == 1) {
            simpleUserDto = userClient.getUserById(account.getUserId());

            if (simpleUserDto == null) {
                final String errorMessage = String.format("Unable to retrieve User For Account %s", account.getId());
                log.error(errorMessage);

                return NewBalanceDto.builder()
                        .id(id)
                        .errMessage(errorMessage)
                        .status(300)
                        .build();
            }
        }

        final String requestId = UUID.randomUUID().toString();

        FundWalletRequest request = FundWalletRequest.builder()
                .id(requestId)
                .amount(dto.getBalance())
                .fundType(Constants.WALLET_ONECARD_FUNDED)
                .paymentVerified(true)
                .closed(true)
                .status(200)
                .actionedBy(Security.getUserName())
                .userId(simpleUserDto == null ? "No UserId For Organization" : simpleUserDto.getId())
                .paymentId(requestId)
                .message(dto.getNarrative())
                .build();

        fundWalletMapper.saveClosedAndVerified(request);

        if (simpleUserDto != null) {
            log.info(String.format("Sending Notification of Admin Wallet Update to %s", simpleUserDto.getEmail()));

            final String message = String.format("Dear %s %s\n\nYour account has been funded by OneCard Admin with %.2f.\nYour New Balance is Now %.2f",
                    simpleUserDto.getFirstName(), simpleUserDto.getUserName(), dto.getBalance(), newBalance);

            MailMessageDto mailMessageDto = MailMessageDto.builder()
                    .to(simpleUserDto.getEmail())
                    .body(message)
                    .subject("Wallet Funded")
                    .build();

            mailService.pushMailMessage(mailMessageDto);

        } // To be Modified to search for E-Mail Addresses of Users in Organization and Notify them accordingly

        final String auditMessage =
                String.format("Account (%s / %s) Balance increased by %.2f to %.2f by (%s)", account.getId(), account.getName(), dto.getBalance(), account.getBalance(), Security.getUserName());
        log.info(auditMessage);
        auditService.auditEvent(auditMessage, Constants.ACCOUNT_BALANCE_UPDATED);
        saveTransaction(dto.getBalance(), account.getId(), Constants.ACCOUNT_WALLET_ADMIN_FUNDED);

        return NewBalanceDto.builder()
                .status(200)
                .balance(newBalance)
                .id(id)
                .build();
    }

    @Transactional
    public WalletResponseDto chargeAccount(WalletRequestDto dto) {
        final String userId = Security.getUserId();

        Account account = Optional.ofNullable(getActiveUserAccount(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Account", "UserId", userId));

        if (account.getBalance().compareTo(dto.getAmount()) >= 0)  {
            BigDecimal newValue = account.getBalance().subtract(dto.getAmount());
            account.setBalance(newValue);
            accountMapper.changeBalance(account);

            final String successMsg = String.format("Updated Balance for Account %s is %.2f", account.getId(), newValue);

            log.info(successMsg);
            auditService.auditEvent(successMsg, Constants.ACCOUNT_CHARGED);

            return WalletResponseDto.builder()
                    .message("Successful")
                    .balance(newValue)
                    .status(200)
                    .build();
        }

        final String errorMsg =
                String.format("Error charging account, Insufficient Balance for UserId %s charging %.2f, balance %.2f",
                        userId,
                        dto.getAmount().doubleValue(),
                        account.getBalance().doubleValue()
                );

        log.error(errorMsg);

        return WalletResponseDto.builder()
                .message("Insufficient Balance")
                .status(300)
                .build();
    }

    public PagedDto<FundWalletRequestDto> findWalletFunding(String userId, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        Page<FundWalletRequest> requests = fundWalletMapper.findByUserId(userId);
        return createFundDto(requests);
    }

    public Optional<List<FundWalletRequestDto>> findWalletRequestReport(WalletReportRequestDto dto) {
        List<FundWalletRequest> requests = fundWalletMapper.findByCriteria(dto);

        if (requests.size() > 0) {
            List<String> ids = requests.stream()
                    .map(FundWalletRequest::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            if (!ids.isEmpty()) {
                UserIdListDto userIdListDto = new UserIdListDto(ids);
                UserEntryListDto userEntries = userClient.getUserEntries(userIdListDto);

                if (userEntries != null && userEntries.getEntries() != null && userEntries.getEntries().size() > 0) {
                    List<FundWalletRequestDto> response = requests.stream()
                            .map(m -> {
                                final FundWalletRequestDto fundWalletRequestDto = fundWalletMapstructMapper.requestToRequestDto(m);

                                if (m.getUserId() != null) {
                                    Optional<UserEntryDto> first = userEntries.getEntries()
                                            .stream()
                                            .filter(x -> x.getId().equals(m.getUserId()))
                                            .findFirst();

                                    first.ifPresent(userEntryDto -> fundWalletRequestDto.setUserName(userEntryDto.getName()));
                                }

                                return fundWalletRequestDto;
                            })
                    .collect(Collectors.toList());

                    return Optional.of(response);
                }

                return Optional.of(requests.stream()
                        .map(fundWalletMapstructMapper::requestToRequestDto)
                        .collect(Collectors.toList()));
            }
        }

        return Optional.empty();
    }

    public static String saveFundWalletRequest(BigDecimal amount, Integer fundType, String userId,
                                               String actionedBy, String narrative) {

        final String id = UUID.randomUUID().toString();

        FundWalletRequest request = FundWalletRequest.builder()
                .id(id)
                .amount(amount)
                .fundType(fundType)
                .paymentVerified(true)
                .closed(true)
                .status(200)
                .userId(userId)
                .actionedBy(actionedBy)
                .paymentId(id)
                .message(narrative)
                .build();

        FundWalletMapper walletMapper = ApplicationContextProvider.getBean(FundWalletMapper.class);
        walletMapper.saveClosedAndVerified(request);

        return id;
    }

    public static void saveTransaction(BigDecimal amount, String accountId, String narrative) {
        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .recipient(accountId)
                .serviceId(100)
                .serviceName(narrative)
                .txAmount(amount)
                .requestId("NOT APPLICABLE")
                .build();

        ApplicationContextProvider.getBean(TransactionMapper.class).save(transaction);
    }

    private void invokeRefundNotification(Account account, BigDecimal amount) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            log.info("invoking Refund Hook {}", account.getWebHook());

            WebHookDto dto = WebHookDto.builder()
                    .id(account.getId())
                    .amount(amount)
                    .narrative("Recharge failed and has been refunded")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(dto), headers);
            restTemplate.exchange(account.getWebHook(), HttpMethod.POST, request, Void.class);
        } catch(Exception ex) {
            log.error("Exception running hook for Account {}", account.getId());
            log.error(ex.getMessage());
        }
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
        PaymentRequestDto dto = paymentClient.checkPayment(id);
        return dto != null ? dto.getVerified() : false;
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
}