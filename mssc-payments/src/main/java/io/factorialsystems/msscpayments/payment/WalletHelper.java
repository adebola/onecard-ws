package io.factorialsystems.msscpayments.payment;

import io.factorialsystems.msscpayments.dao.PaymentMapper;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import io.factorialsystems.msscpayments.external.client.AccountClient;
import io.factorialsystems.msscpayments.mapper.PaymentRequestMapper;
import io.factorialsystems.msscpayments.payment.wallet.InitializeWalletTransactionRequest;
import io.factorialsystems.msscpayments.payment.wallet.InitializeWalletTransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletHelper implements Payment {
    private static final Integer SUCCESS = 200;
    private static final Integer INSUFFICIENT_BALANCE = 300;

    private final AccountClient accountClient;
    private final PaymentMapper paymentMapper;
    private final PaymentRequestMapper paymentRequestMapper;

    @Override
    @SneakyThrows
    public PaymentRequestDto initializePayment(PaymentRequestDto dto) {
        String id = UUID.randomUUID().toString();

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .id(id)
                .amount(dto.getAmount())
                .paymentMode(dto.getPaymentMode())
                .build();

        paymentMapper.save(paymentRequest);

        InitializeWalletTransactionRequest txRequest = InitializeWalletTransactionRequest.builder()
                .requestId(id)
                .amount(dto.getAmount())
                .build();

        InitializeWalletTransactionResponse response = accountClient.initializeWalletTransaction(txRequest);

        if (response != null && response.getStatus() != null) {
            Integer status = response.getStatus();

            if (Objects.equals(status, SUCCESS)) {
                paymentRequest.setMessage("SUCCESS");
                paymentRequest.setVerified(true);
                log.info(String.format("Successful Wallet Payment for (%s) Amount (%.2f)", paymentRequest.getId(), paymentRequest.getAmount().doubleValue()));
            } else if (Objects.equals(status, INSUFFICIENT_BALANCE)) {
                paymentRequest.setMessage("INSUFFICIENT FUNDS");
                paymentMapper.update(paymentRequest);
                log.error(String.format("UnSuccessful Wallet Payment for (%s) Amount (%.2f) Insufficient Funds", paymentRequest.getId(), paymentRequest.getAmount().doubleValue()));
            } else {
                log.error("Error Status in Payment");
                throw new RuntimeException("Invalid Wallet Payment Status");
            }

            paymentRequest.setStatus(status);
            paymentRequest.setBalance(response.getBalance());
            paymentMapper.update(paymentRequest);

            return paymentRequestMapper.requestToDto(paymentRequest);
        }

        throw new RuntimeException("Initialize Wallet Transaction results are indeterminable");
    }

    @Override
    public PaymentRequestDto checkPaymentValidity(PaymentRequest paymentRequest) {
        return null;
    }
}
