package io.factorialsystems.msscwallet.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscwallet.config.JMSConfig;
import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.dao.TransactionMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.domain.Transaction;
import io.factorialsystems.msscwallet.domain.User;
import io.factorialsystems.msscwallet.domain.UserWallet;
import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceJMSListener {
    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;
    private final RestTemplate restTemplate;
    private final AccountMapper accountMapper;
    private final AccountService accountService;
    private final TransactionMapper transactionMapper;

    @Value("${api.host.baseurl}")
    private String baseUrl;


    public static final String WALLET_REFUND_QUEUE = "wallet-refund-queue";

    @SneakyThrows
    @JmsListener(destination = JMSConfig.WALLET_REFUND_QUEUE)
    public void listenForRefund(String jsonData)  {
        AsyncRefundRequestDto request = objectMapper.readValue(jsonData, AsyncRefundRequestDto.class);
        AccountService accountService = applicationContext.getBean(AccountService.class);
        accountService.refundWallet(request);
    }

    @JmsListener(destination = JMSConfig.NEW_USER_WALLET_QUEUE)
    public void listenForNewUser(String jsonData)  {

        try {
            User user = objectMapper.readValue(jsonData, User.class);

            Account account = Account.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(user.getId())
                    .accountType(1)
                    .createdBy(user.getUsername())
                    .name(user.getUsername())
                    .activated(true)
                    .build();

            accountMapper.save(account);
            UserWallet userWallet = UserWallet.builder()
                    .userId(user.getId())
                    .walletId(account.getId())
                    .build();

            jmsTemplate.convertAndSend(JMSConfig.UPDATE_USER_WALLET_QUEUE, objectMapper.writeValueAsString(userWallet));

            log.info(String.format("Creating Wallet for User (%s)/(%s)", user.getId(), user.getUsername()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = JMSConfig.NEW_TRANSACTION_QUEUE)
    public void listenForTransaction (String jsonData) {

        try {
            RequestTransactionDto dto = objectMapper.readValue(jsonData, RequestTransactionDto.class);

            Account account =
                    dto.getUserId() == null ? accountMapper.findAnonymousAccount() : accountMapper.findAccountByUserId(dto.getUserId());

            if (account == null) {
                final String message = String.format("Error saving New Transaction, Unable to find Account for User (%s)", dto.getUserId());
                log.error(message);

                return;
            }

            log.info("Retrieved Account for Transaction ID {}, UserName {}", account.getId(), account.getName());

            String action = null;
            int serviceId = 0;

            if (dto.getServiceId() != null) {
                Optional<ServiceActionDto> actionDto
                        = Optional.ofNullable(restTemplate.getForObject(baseUrl + "api/v1/serviceprovider/service/" + dto.getServiceId(), ServiceActionDto.class));

                if (actionDto.isEmpty()) {
                    final String message = "Error Retrieving Service Action for Service Id " + dto.getServiceId();
                    log.error(message);
                   return;
                }

                action = actionDto.get().getServiceName();
                serviceId = dto.getServiceId();
            } else {
                action = "Bulk Recharge";
            }

            log.info("Retrieved ServiceAction for Transaction {}", account.getId());

            Transaction transaction = Transaction.builder()
                    .serviceId(serviceId)
                    .serviceName(action)
                    .accountId(account.getId())
                    .chargeAccountId(account.getChargeAccountId())
                    .txAmount(dto.getServiceCost())
                    .requestId(dto.getRequestId())
                    .recipient(dto.getRecipient())
                    .build();

            transactionMapper.save(transaction);

            log.info("Transaction Saved Successfully {}", transaction.getId());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = JMSConfig.DELETE_ACCOUNT_QUEUE)
    public void listenForDeleteAccount(String jsonData) {

        try {
            DeleteAccountDto dto = objectMapper.readValue(jsonData, DeleteAccountDto.class);

            log.info("Deleting Account: {}, Action By {}", dto.getId(), dto.getDeletedBy());

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("id", dto.getId());
            parameters.put("deletedBy", dto.getDeletedBy());

            accountMapper.deleteAccount(parameters);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = JMSConfig.ADD_ORGANIZATION_ACCOUNT_QUEUE)
    public void listenForAddOrganizationToUser(String jsonData) {
        try {
            UserOrganizationAmendDto dto = objectMapper.readValue(jsonData, UserOrganizationAmendDto.class);

            if (dto == null || dto.getOrganizationId() == null || dto.getUserId() == null) {
                log.error("Error Adding Organization Wallet to User, Null Values received");
                return;
            }

            // Get Organization Account
            Account orgAccount = accountMapper.findAccountByUserId(dto.getOrganizationId());

            if (orgAccount == null || orgAccount.getId() == null) {
                log.error(String.format("Error Adding Organization Wallet (%s) to User (%s), Organization Not Found", dto.getUserId(), dto.getOrganizationId()));
                return;
            }

            // Get User Account
            Account userAccount = accountMapper.findAccountByUserId(dto.getUserId());

            if (userAccount == null) {
                log.error(String.format("Error Adding Organization Wallet (%s) to User (%s), User Not Found", dto.getUserId(), dto.getOrganizationId()));
                return;
            }

            log.info(String.format("linking User Account %s with Organization %s", userAccount.getName(), orgAccount.getName()));

            HashMap<String, String> parameters = new HashMap<>();
            parameters.put("organizationId", orgAccount.getId());
            parameters.put("id", userAccount.getId());

            accountMapper.addOrganizationWallet(parameters);

        }  catch (JsonProcessingException e) {
            log.error("Error Adding Organization Wallet to User : " + e.getMessage());
        }
    }

    @JmsListener(destination = JMSConfig.REMOVE_ORGANIZATION_ACCOUNT_QUEUE)
    public void listenForRemoveOrganizationFromUser(String jsonData) {
        try {
            UserOrganizationAmendDto dto = objectMapper.readValue(jsonData, UserOrganizationAmendDto.class);

            if (dto == null || dto.getUserId() == null) {
                log.error("Error Removing Organization Wallet from User, Null Values Found");
                return;
            }

            log.info(String.format("De-linking User Account %s from Organization", dto.getUserId()));

            accountMapper.removeOrganizationWallet(dto.getUserId());
        }  catch (JsonProcessingException e) {
            log.error("Error Removing Organization Wallet from User : " + e.getMessage());
        }
    }

    @JmsListener(destination = JMSConfig.SEND_MAIL_QUEUE)
    public void listenForMailMessage(String jsonData) {

        try {
            MailMessageDto mailMessageDto = objectMapper.readValue(jsonData, MailMessageDto.class);
            accountService.sendMailMessage(mailMessageDto);
        } catch (JsonProcessingException e) {
            log.error("Error Processing JMS Message to send E-mail Reason : " + e.getMessage());
        }
    }

    @JmsListener(destination = JMSConfig.NEW_RECHARGE_PROVIDER_WALLET_QUEUE)
    public void listenForNewRechargeProvider(String jsonData) {

    }
}
