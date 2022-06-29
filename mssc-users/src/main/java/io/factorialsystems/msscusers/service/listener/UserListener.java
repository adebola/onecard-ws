package io.factorialsystems.msscusers.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscusers.config.JMSConfig;
import io.factorialsystems.msscusers.dao.UserMapper;
import io.factorialsystems.msscusers.domain.User;
import io.factorialsystems.msscusers.domain.UserWallet;
import io.factorialsystems.msscusers.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserListener {
    private final ObjectMapper objectMapper;
    private final ApplicationContext applicationContext;

    @JmsListener(destination = JMSConfig.NEW_USER_QUEUE)
    public void listenForNewUser(String jsonData) throws IOException {
        if (jsonData != null) {
            User user = objectMapper.readValue(jsonData, User.class);

            if (user.getId() != null) {
                 UserMapper userMapper = applicationContext.getBean(UserMapper.class);
                 userMapper.save(user);

                log.info(String.format("New User (%s),(%s) Registered and saved to local database",
                        user.getId(), user.getUsername()));

                JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);
                jmsTemplate.convertAndSend(JMSConfig.NEW_USER_WALLET_QUEUE, jsonData);
            }
        }
    }

    @JmsListener(destination = JMSConfig.UPDATE_USER_WALLET_QUEUE)
    public void listenForUserWalletUpdate(String jsonData) throws IOException {

        if (jsonData != null) {
            UserWallet userWallet = objectMapper.readValue(jsonData, UserWallet.class);

            if (userWallet != null) {
                log.info(String.format("Updating User %s with Wallet %s", userWallet.getUserId(), userWallet.getWalletId()));

                UserMapper userMapper = applicationContext.getBean(UserMapper.class);
                userMapper.update(
                        User.builder()
                                .id(userWallet.getUserId())
                                .walletId(userWallet.getWalletId())
                                .build()
                );
            }
        }
    }

    @JmsListener(destination = JMSConfig.LOGIN_QUEUE)
    public void listenForUserLogin(String jsonData) throws IOException {

        if (jsonData != null) {
           String id = objectMapper.readValue(jsonData, String.class);

            UserService userService = applicationContext.getBean(UserService.class);
            userService.sendLoginMessage(id);
        }
    }
}
