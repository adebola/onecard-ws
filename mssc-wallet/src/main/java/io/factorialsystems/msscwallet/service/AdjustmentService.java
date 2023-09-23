package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.dao.AccountLedgerMapper;
import io.factorialsystems.msscwallet.dao.AccountMapper;
import io.factorialsystems.msscwallet.dao.AdjustmentMapper;
import io.factorialsystems.msscwallet.domain.Account;
import io.factorialsystems.msscwallet.domain.AccountLedgerEntry;
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
import org.springframework.beans.factory.annotation.Value;
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
    private final AuditService auditService;
    private final AccountMapper accountMapper;
    private final AdjustmentMapper adjustmentMapper;
    private final AccountLedgerMapper accountLedgerMapper;

    @Value("${mail.secret}")
    private String mailSecret;

    @Transactional
    public AdjustmentResponseDto adjustBalance(AdjustmentRequestDto dto) {

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(String.format("Adjustment Amount must be greater than Zero, Value submitted %.2f", dto.getAmount()));
        }

        Account account = accountMapper.findAccountById(dto.getAccountId());

        if (account != null) {
            BigDecimal oldBalance = account.getBalance();

            // Change the Account Balance
            final BigDecimal newBalance = oldBalance.subtract(dto.getAmount());

            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Adjustment will take User Account Balance into negative");
            }

            account.setBalance(newBalance);
            accountMapper.changeBalance(account);

            //BigDecimal delta = dto.getAmount().subtract(oldBalance);

            // Save the corresponding FundWallet request
            final String requestId = AccountService.saveFundWalletRequest(dto.getAmount(),
                    Constants.WALLET_ONECARD_ADJUSTED,
                    account.getUserId(),
                    Security.getUserName(),
                    dto.getNarrative());

            final String id = UUID.randomUUID().toString();

            // Save the Adjustment
            Adjustment adjustment = Adjustment.builder()
                    .id(id)
                    .fundWalletRequestId(requestId)
                    .narrative(dto.getNarrative())
                    .adjustedBy(Security.getUserId())
                    .adjustedValue(dto.getAmount())
                    .previousValue(oldBalance)
                    .accountId(dto.getAccountId())
                    .build();

            adjustmentMapper.save(adjustment);

//            int status;
            final String ledgerMessage = String.format("User Account Adjusted by %.2f by %s", dto.getAmount(), Security.getUserName());

//            if (delta.compareTo(BigDecimal.ZERO) < 0) {
//                status = AccountService.LEDGER_OPERATION_SYSTEM_DEBIT;
//            } else {
//                status = AccountService.LEDGER_OPERATION_SYSTEM_CREDIT;
//            }

            AccountLedgerEntry entry = AccountLedgerEntry.builder()
                    .id(UUID.randomUUID().toString())
                    .accountId(account.getId())
                    .operation(AccountService.LEDGER_OPERATION_SYSTEM_DEBIT)
                    .amount(dto.getAmount())
                    .description(ledgerMessage)
                    .build();

            accountLedgerMapper.save(entry);

            SimpleUserDto simpleUserDto = userClient.getUserById(account.getUserId());

            // Send the Mail
            if (simpleUserDto != null) {
                final String message = String.format("Dear %s %s\n\nYour account has been adjusted from %.2f to %.2f by Onecard Admin. Please contact Onecard for further enquiries",
                        simpleUserDto.getFirstName(), simpleUserDto.getLastName(), oldBalance, dto.getAmount());

                MailMessageDto mailMessageDto = MailMessageDto.builder()
                        .subject("Wallet Adjustment")
                        .to(simpleUserDto.getEmail())
                        .body(message)
                        .secret(mailSecret)
                        .build();

                mailService.pushMailMessage(mailMessageDto);
            }

            // Save the Transaction
            AccountService.saveTransaction(
                    dto.getAmount().multiply(BigDecimal.valueOf(-1)),
                    dto.getAccountId(),
                    Constants.ACCOUNT_BALANCE_ADJUSTED
            );

            // Save Audit
            final String auditMessage = String.format("Account Balance adjusted from %.2f to %.2f by %s", oldBalance, dto.getAmount(), Security.getUserName());
            auditService.auditEvent(auditMessage, Constants.ACCOUNT_BALANCE_ADJUSTED);

            return AdjustmentResponseDto.builder()
                    .id(id)
                    .status(200)
                    .balance(newBalance)
                    .message("Success")
                    .build();
        }

        return AdjustmentResponseDto.builder()
                .status(300)
                .message("Adjustment Failed, account not found")
                .build();
    }
}
