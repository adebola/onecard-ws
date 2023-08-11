package io.factorialsystems.msscwallet.tasks;

import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    @Value("${api.host.baseurl}")
    private String apiHost;

    private final AccountMapper accountMapper;

//    @Scheduled(fixedRate = 3600000)
    public void reportThresholds() {
        log.info("Running ReportThreshold............");

        List<Account> accounts = accountMapper.findLowThresholdAccounts();

//        if (!accounts.isEmpty()) {
//            List<String> users = extractCategory(accounts, USER_ACCOUNT);
//
//            if (users.size() > 0) {
//                RestTemplate restTemplate = new RestTemplate();
//                restTemplate.getInterceptors().add(new RestTemplateInterceptor());
//                LowThresholdNotificationDto low = new LowThresholdNotificationDto(users);
//            }
//        }
    }

    private List<String> extractCategory(List<Account> accounts, Integer categoryId) {
        return  accounts.stream()
                .filter(a -> Objects.equals(a.getAccountType(), categoryId))
                .map(Account::getUserId)
                .collect(Collectors.toList());
    }
}
