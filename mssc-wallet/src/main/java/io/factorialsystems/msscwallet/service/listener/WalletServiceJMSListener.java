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
import io.factorialsystems.msscwallet.dto.DeleteAccountDto;
import io.factorialsystems.msscwallet.dto.RequestTransactionDto;
import io.factorialsystems.msscwallet.dto.ServiceActionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final RestTemplate restTemplate;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;

    @Value("${api.host.baseurl}")
    private String baseUrl;

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

    @JmsListener(destination = JMSConfig.NEW_RECHARGE_PROVIDER_WALLET_QUEUE)
    public void listenForNewRechargeProvider(String jsonData) {

    }
}
