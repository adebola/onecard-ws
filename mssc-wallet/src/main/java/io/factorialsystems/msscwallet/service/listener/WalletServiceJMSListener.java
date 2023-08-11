package io.factorialsystems.msscwallet.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscwallet.config.JMSConfig;
import io.factorialsystems.msscwallet.domain.User;
import io.factorialsystems.msscwallet.dto.*;
import io.factorialsystems.msscwallet.service.AccountService;
import io.factorialsystems.msscwallet.service.MailService;
import io.factorialsystems.msscwallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceJMSListener {
    private final MailService mailService;
    private final ObjectMapper objectMapper;
    private final AccountService accountService;
    private final TransactionService transactionService;

    @SneakyThrows
    @JmsListener(destination = JMSConfig.WALLET_REFUND_QUEUE)
    public void listenForRefund(String jsonData)  {
        AsyncRefundRequestDto request = objectMapper.readValue(jsonData, AsyncRefundRequestDto.class);
        accountService.asyncRefundWallet(request);
    }

    @SneakyThrows
    @JmsListener(destination = JMSConfig.NEW_USER_WALLET_QUEUE)
    public void listenForNewUser(String jsonData)  {
        User user = objectMapper.readValue(jsonData, User.class);
        accountService.asyncCreateUserAccount(user);
    }

    @SneakyThrows
    @JmsListener(destination = JMSConfig.NEW_TRANSACTION_QUEUE)
    public void listenForTransaction (String jsonData) {
        RequestTransactionDto dto = objectMapper.readValue(jsonData, RequestTransactionDto.class);
        transactionService.asyncSaveTransaction(dto);
    }

    @SneakyThrows
    @JmsListener(destination = JMSConfig.DELETE_ACCOUNT_QUEUE)
    public void listenForDeleteAccount(String jsonData) {
        DeleteAccountDto dto = objectMapper.readValue(jsonData, DeleteAccountDto.class);
        accountService.asyncDeleteAccount(dto);
    }

    @SneakyThrows
    @JmsListener(destination = JMSConfig.ADD_ORGANIZATION_ACCOUNT_QUEUE)
    public void listenForAddOrganizationToUser(String jsonData) {
        UserOrganizationAmendDto dto = objectMapper.readValue(jsonData, UserOrganizationAmendDto.class);
        accountService.asyncAddUserToOrganization(dto);
    }

    @SneakyThrows
    @JmsListener(destination = JMSConfig.REMOVE_ORGANIZATION_ACCOUNT_QUEUE)
    public void listenForRemoveOrganizationFromUser(String jsonData) {
        UserOrganizationAmendDto dto = objectMapper.readValue(jsonData, UserOrganizationAmendDto.class);
        accountService.asyncRemoveUserFromOrganization(dto);
    }

    @SneakyThrows
    @JmsListener(destination = JMSConfig.SEND_MAIL_QUEUE)
    public void listenForMailMessage(String jsonData) {
        MailMessageDto mailMessageDto = objectMapper.readValue(jsonData, MailMessageDto.class);
        mailService.sendMailWithOutAttachment(mailMessageDto);
    }

    @JmsListener(destination = JMSConfig.NEW_RECHARGE_PROVIDER_WALLET_QUEUE)
    public void listenForNewRechargeProvider(String jsonData) {
    }
}
