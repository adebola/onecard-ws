package io.factorialsystems.msscwallet.service.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscwallet.config.JMSConfig;
import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.domain.User;
import io.factorialsystems.msscwallet.domain.UserWallet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceJMSListener {
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    @JmsListener(destination = JMSConfig.NEW_USER_WALLET_QUEUE)
    public void listenForNewUser(String jsonData)  {

        try {
            User user = objectMapper.readValue(jsonData, User.class);
            AccountMapper accountMapper = applicationContext.getBean(AccountMapper.class);

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

            JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);
            jmsTemplate.convertAndSend(JMSConfig.UPDATE_USER_WALLET_QUEUE, objectMapper.writeValueAsString(userWallet));

            log.info(String.format("Creating Wallet for User (%s)/(%s)", user.getId(), user.getUsername()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
