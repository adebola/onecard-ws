package io.factorialsystems.msscwallet.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscwallet.config.JMSConfig;
import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.dao.AccountVerificationMapper;
import io.factorialsystems.msscwallet.dao.BVNVerificationMapper;
import io.factorialsystems.msscwallet.dao.SMSVerificationMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.domain.AccountVerification;
import io.factorialsystems.msscwallet.domain.BVNVerification;
import io.factorialsystems.msscwallet.domain.SMSVerification;
import io.factorialsystems.msscwallet.dto.AsyncSMSMessageDto;
import io.factorialsystems.msscwallet.dto.SMSVerificationRequestDto;
import io.factorialsystems.msscwallet.dto.SimpleUserDto;
import io.factorialsystems.msscwallet.dto.kyc.BVNVerificationResponseDto;
import io.factorialsystems.msscwallet.exception.ResourceNotFoundException;
import io.factorialsystems.msscwallet.external.client.UserClient;
import io.factorialsystems.msscwallet.utils.Security;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KycService {
    private final UserClient userClient;
    private final JmsTemplate messageSender;
    private final ObjectMapper objectMapper;
    private final AccountMapper accountMapper;
    private final SMSVerificationMapper smsVerificationMapper;
    private final BVNVerificationMapper bvnVerificationMapper;
    private final AccountSettingService accountSettingService;
    private final AccountVerificationMapper accountVerificationMapper;

    private final Random random = new Random();

    @Value("${verification.appid}")
    private String verificationAppId;

    @Value("${verification.key}")
    private String verificationKey;

    @Value("${verification.url}")
    private String verificationUrl;

    // Generate and Send SMS for Verification
    @Transactional
    @SneakyThrows
    public Map<String, String> startSMSVerification(String msisdn) {
        final String id = UUID.randomUUID().toString();
        final String code = String.format("%06d", random.nextInt(1_000_000));

        final Account account = accountMapper.findAccountByUserId(Security.getUserId());

        if (account == null) {
            throw new RuntimeException(String.format("Unable to find Account for User %s", Security.getUserId()));
        }

        if (!Objects.equals(account.getAccountType(), AccountService.USER_ACCOUNT)) {
            throw new RuntimeException(
                    String.format("SMS verification can only be applied to USER Accounts, please contact support, %s is a User", Security.getUserId())
            );
        }

        if (smsVerificationMapper.checkVerifiedExistsById(account.getId())) {
            throw new RuntimeException("This Account has already been SMS Verified");
        }

        AsyncSMSMessageDto dto = AsyncSMSMessageDto.builder()
                .to(msisdn)
                .message(String.format("Please use this code for Verification %s", code))
                .userId(Security.getUserId())
                .email(Security.getEmail())
                .build();

        messageSender.convertAndSend(JMSConfig.SEND_SMS_QUEUE, objectMapper.writeValueAsString(dto));

        SMSVerification smsVerification = SMSVerification.builder()
                .id(id)
                .code(code)
                .expiry(OffsetDateTime.now().plusMinutes(5))
                .accountId(account.getId())
                .msisdn(msisdn)
                .build();

        smsVerificationMapper.save(smsVerification);
        return getSMSVerificationMap(id, smsVerification.getExpiry());
    }

    @Transactional
    public Map<String, String> finalizeSMSVerification(SMSVerificationRequestDto dto) {
        final OffsetDateTime now = OffsetDateTime.now();

        final SMSVerification smsVerification = smsVerificationMapper.findById(dto.getId());

        if (smsVerification == null) {
            throw new ResourceNotFoundException("SMSVerification", "id", dto.getId());
        }

        Account account = accountMapper.findAccountById(smsVerification.getAccountId());

        if (account == null) {
            throw new ResourceNotFoundException("Account", "id", smsVerification.getAccountId());
        }

        if (Objects.equals(account.getAccountType(), AccountService.USER_ACCOUNT)) {
            if (!account.getUserId().equals(Security.getUserId())) {
                throw new AccessDeniedException(String.format("Access Denied Verifying Account %s", dto.getId()));
            }
        }

        Map<String, String> value = new HashMap<>();

        if (smsVerification.getExpiry().isBefore(now)) {
            value.put("status", "Failed");
            value.put("message", "Verification code may have expired, please request another");

            return value;
        }

        if (!smsVerification.getCode().equals(dto.getCode())) {
            value.put("status", "Failed");
            value.put("message", "Wrong Verification code, please try again");

            return value;
        }

        smsVerificationMapper.verify(dto.getId());
        value.put("status", "Successful");
        value.put("message", "SMS Verification Successful");

        return value;
    }

    @Transactional
    public Map<String, String> bvnVerification(String bvn) {
        final String userId = Security.getUserId();

        Account account = accountMapper.findAccountByUserId(userId);

        if (account == null) {
            throw new RuntimeException(String.format("Account for User %s Not Found", userId));
        }

        final SMSVerification smsVerification = smsVerificationMapper.findByAccountIdVerified(account.getId());

        if (smsVerification == null) {
            throw new RuntimeException("Please do SMS Verification before BVN Verification");
        }

        Map<String, String> map = new HashMap<>();

        if (!bvn.matches("\\d+") || bvn.length() != 11) {
            throw new RuntimeException(String.format("BVN should be NUMBERS only and 11 digits : %s", bvn));
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        headers.set("x-api-key", verificationKey);
        headers.set("app-id", verificationAppId);

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
        requestBody.add("number", bvn);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBody, headers);

        final String response =
                restTemplate.postForObject(verificationUrl, entity, String.class);

        log.info("BVN Response : {}", response);

        JsonNode apiNode = null;

        try {
            apiNode = objectMapper.readTree(response).path("status");
            Boolean status = objectMapper.readValue(apiNode.toString(), Boolean.class);

            if (status) {
                final String verificationId = UUID.randomUUID().toString();
                BVNVerificationResponseDto responseDto = objectMapper.readValue(response, BVNVerificationResponseDto.class);
                BVNVerification bvnVerification = BVNVerification.builder()
                        .id(verificationId)
                        .accountId(account.getId())
                        .bvn(bvn)
                        .dateOfBirth(responseDto.getData().getDateOfBirth())
                        .firstName(responseDto.getData().getFirstName())
                        .lastName(responseDto.getData().getLastName())
                        .middleName(responseDto.getData().getMiddleName())
                        .phoneNumber(responseDto.getData().getPhoneNumber())
                        .build();

                log.info("Upstream BVN Verification Success {}", response );

                if (compareBVNDetails(smsVerification, bvnVerification)) {
                    bvnVerification.setStatus("SUCCESS");
                    bvnVerification.setVerifiedOn(OffsetDateTime.now());
                    bvnVerification.setVerified(true);

                    bvnVerificationMapper.save(bvnVerification);

                    AccountVerification accountVerification = AccountVerification.builder()
                            .smsVerificationId(smsVerification.getId())
                            .accountId(account.getId())
                            .bvnVerificationId(verificationId)
                            .verifiedBy(Security.getUserName())
                            .build();

                    accountVerificationMapper.save(accountVerification);

                    Map<String, String> verifyMap = new HashMap<>();
                    verifyMap.put("id", account.getId());
                    verifyMap.put("telephone", smsVerification.getMsisdn());

                    accountMapper.verifyAccount(verifyMap);

                    map.put("status", "SUCCESS");
                    map.put("message", "BVN and Account Verification Successful");

                    log.info("BVN Verification Successful, Client fully Verified");
                } else {
                    bvnVerification.setStatus("FAILED_COMPARISON");

                    map.put("status", "FAILED");
                    map.put("message", "Inconsistent bio-data");
                    bvnVerificationMapper.save(bvnVerification);

                    log.info("BVN Verification Comparison Not Successful, Client Not Verified");
                }
            } else {
                BVNVerification bvnVerification = BVNVerification.builder()
                        .id(UUID.randomUUID().toString())
                        .accountId(account.getId())
                        .bvn(bvn)
                        .status(("FAILED_PROVIDER"))
                        .build();

                map.put("status", "FAILED");
                apiNode = objectMapper.readTree(response).path("message");

                final String s;

                if (apiNode == null) {
                    s = "Verification Failed";
                } else {
                    s = String.format("Verification Failed Reason : %s", apiNode);
                }

                log.error(s);
                map.put("message", s);

                bvnVerificationMapper.save(bvnVerification);
                log.info("BVN Verification Failed, Client Not Verified");
            }
        } catch (JsonProcessingException e) {
            log.error("Error Processing JSON {}", response);
            log.error(e.getMessage());

            map.put("status", "FAILED");
            map.put("message", "Parse Error, Please contact Onecard Support");
        }

        return map;
    }

    public Map<String, String> getAdminStatus(String id) {
        Map<String, String> map = getStatus(id);
        final AccountVerification accountVerification = accountVerificationMapper.findByUserId(id);

        if (accountVerification != null) {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss");
            map.put("VerifiedBy", accountVerification.getVerifiedBy());
            map.put("VerifiedOn", dateTimeFormatter.format(accountVerification.getVerifiedOn()));
        }

        return map;
    }

    public Map<String, String> getUserStatus() {
        return getStatus(Security.getUserId());
    }

    private Map<String, String> getStatus(String id) {
        final Map<String, String> map = new HashMap<>();

        if (!smsVerificationMapper.checkVerifiedExistsByUserId(id)) {
            map.put("sms", "Not Verified");
            map.put("bvn", "Not Verified");

            return map;
        }

        map.put("sms", "Verified");

        if (bvnVerificationMapper.checkIfExistsByUserId(id)) {
            map.put("bvn", "Verified");
        } else {
            map.put("bvn", "Not Verified");
        }

        return map;
    }

    private boolean compareBVNDetails(SMSVerification smsVerification, BVNVerification bvnVerification) {
        SimpleUserDto user = userClient.getUserById(Security.getUserId());

        if (user == null) {
            throw new RuntimeException(String.format("Unable to retrieve User %s from the User service", Security.getUserId()));
        }

        boolean matchByFirstName = accountSettingService
                .getAccountSetting(AccountSettingService.SETTINGS_FIRST_NAME_COMPARE)
                .getValue().equals("1");

        if (matchByFirstName) {
            if (!user.getFirstName().equalsIgnoreCase(bvnVerification.getFirstName())) {
                log.error("BVN First Name Mismatch OnSystem {}, BVN {}", user.getFirstName(), bvnVerification.getFirstName());
                return false;
            }
        }

        boolean matchByLastName = accountSettingService
                .getAccountSetting(AccountSettingService.SETTINGS_LAST_NAME_COMPARE)
                .getValue().equals("1");

        if (matchByLastName) {
            if (!user.getLastName().equalsIgnoreCase(bvnVerification.getLastName())) {
                log.error("BVN Last Name Mismatch OnSystem {}, BVN {}", user.getLastName(), bvnVerification.getLastName());
                return false;
            }
        }

        boolean matchByTelephone = accountSettingService
                .getAccountSetting(AccountSettingService.SETTINGS_TELEPHONE_COMPARE)
                .getValue().equals("1");

        if (matchByTelephone) {
            if (!smsVerification.getMsisdn().equalsIgnoreCase(bvnVerification.getPhoneNumber())) {
                log.error("BVN Telephone Number Mismatch SMS Verified {}, BVN {}", smsVerification.getMsisdn(), bvnVerification.getPhoneNumber());
                return false;
            }
        }

        return true;
    }

    private Map<String, String> getSMSVerificationMap(String id, OffsetDateTime expiry) {
        Map<String, String> value = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        value.put("verificationId", id);
        value.put("status", "Success");
        value.put("Expiry", formatter.format(expiry));

        return value;
    }
}
