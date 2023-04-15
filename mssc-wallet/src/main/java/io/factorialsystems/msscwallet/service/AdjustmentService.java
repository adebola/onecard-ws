package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.dao.AdjustmentMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.domain.Adjustment;
import io.factorialsystems.msscwallet.dto.AdjustmentRequestDto;
import io.factorialsystems.msscwallet.dto.AdjustmentResponseDto;
import io.factorialsystems.msscwallet.dto.MailMessageDto;
import io.factorialsystems.msscwallet.dto.SimpleUserDto;
import io.factorialsystems.msscwallet.external.client.UserClient;
import io.factorialsystems.msscwallet.utils.Constants;
import io.factorialsystems.msscwallet.utils.Security;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdjustmentService {
    private final UserClient userClient;
    private final MailService mailService;
    private final AccountMapper accountMapper;
    private final AdjustmentMapper adjustmentMapper;

    @Transactional
    public AdjustmentResponseDto adjustBalance(AdjustmentRequestDto dto) {
        Account account = accountMapper.findAccountById(dto.getAccountId());

        if (account != null) {
            BigDecimal oldBalance = account.getBalance();

            // Change the Account Balance
            account.setBalance(dto.getAmount());
            accountMapper.changeBalance(account);

            final String id = UUID.randomUUID().toString();

            // Save the Adjustment
            Adjustment adjustment = Adjustment.builder()
                    .id(id)
                    .narrative(dto.getNarrative())
                    .adjustedBy(Security.getUserId())
                    .adjustedValue(dto.getAmount())
                    .previousValue(oldBalance)
                    .accountId(dto.getAccountId())
                    .build();

            adjustmentMapper.save(adjustment);

            BigDecimal delta = dto.getAmount().subtract(oldBalance);

            // Save the corresponding FundWallet request
            AccountService.saveFundWalletRequest(delta, Constants.WALLET_ONECARD_ADJUSTED, account.getUserId(),
                    Security.getUserName(), dto.getNarrative());

            SimpleUserDto simpleUserDto = userClient.getUserById(account.getUserId());

            // Send the Mail
            if (simpleUserDto != null) {

                final String message = String.format("Dear %s %s\n\nYour account has been adjusted from %.2f to %.2f by Onecard Admin. Please contact Onecard for further enquiries",
                        simpleUserDto.getFirstName(), simpleUserDto.getLastName(), oldBalance, dto.getAmount());

                MailMessageDto mailMessageDto = MailMessageDto.builder()
                        .subject("Wallet Adjustment")
                        .to(simpleUserDto.getEmail())
                        .body(message)
                        .build();

                mailService.pushMailMessage(mailMessageDto);
            }

            // Save the Transaction
            AccountService.saveTransaction(delta, dto.getAccountId(), Constants.ACCOUNT_BALANCE_ADJUSTED);

            return AdjustmentResponseDto.builder()
                    .id(id)
                    .status(200)
                    .message("Success")
                    .build();
        }

        return AdjustmentResponseDto.builder()
                .status(300)
                .message("Adjustment Failed, account not found")
                .build();
    }
}
