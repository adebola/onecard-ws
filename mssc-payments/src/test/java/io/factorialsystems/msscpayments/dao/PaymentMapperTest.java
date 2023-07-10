package io.factorialsystems.msscpayments.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.domain.RefundRequest;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@CommonsLog
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentMapperTest {

    @Autowired
    PaymentMapper paymentMapper;

    @Test
    void findAll() {
        final Page<PaymentRequest> all = paymentMapper.findAll();
        assertThat(all.getResult().size()).isGreaterThan(1);
        log.info(all);
    }

    @Test
    void findById() {
        final String id = "0249d58f-0470-4dd2-a08f-36958f6a85c5";
        final PaymentRequest paymentRequest = paymentMapper.findById(id);
        assertThat(paymentRequest).isNotNull();
        assertThat(paymentRequest.getId()).isEqualTo(id);
        log.info(paymentRequest);
    }

    @Test
    @Rollback
    @Transactional
    void save() {
        final String id = UUID.randomUUID().toString();
        final String paymentMode = "wallet";

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .id(id)
                .paymentMode(paymentMode)
                .amount(new BigDecimal(1200))
                .build();

        paymentMapper.save(paymentRequest);

        PaymentRequest savedRequest = paymentMapper.findById(id);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isEqualTo(id);
        assertThat(savedRequest.getPaymentMode()).isEqualTo(paymentMode);

        log.info(savedRequest);
    }

    @Test
    @Rollback
    @Transactional
    void verifyByReference() {
        final String id = UUID.randomUUID().toString();
        final String paymentMode = "wallet";
        final String reference = "x2z5%v34jks";

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .id(id)
                .paymentMode(paymentMode)
                .amount(new BigDecimal(1200))
                .reference(reference)
                .build();

        paymentMapper.save(paymentRequest);

        PaymentRequest request = paymentMapper.findById(id);
        assertThat(request.getVerified()).isEqualTo(false);
        assertThat(request.getReference()).isEqualTo(reference);
        assertThat(request.getPaymentVerified()).isNull();

        paymentMapper.verifyByReference(reference);

        request = paymentMapper.findById(id);
        assertThat(request.getReference()).isEqualTo(reference);
        assertThat(request.getVerified()).isEqualTo(true);
        assertThat(request.getPaymentVerified()).isNotNull();

        log.info(request);
    }

    @Test
    @Rollback
    @Transactional
    void verifyById() {
        final String id = UUID.randomUUID().toString();
        final String paymentMode = "wallet";

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .id(id)
                .paymentMode(paymentMode)
                .amount(new BigDecimal(1200))
                .build();

        paymentMapper.save(paymentRequest);

        PaymentRequest request = paymentMapper.findById(id);
        assertThat(request.getVerified()).isEqualTo(false);
        assertThat(request.getPaymentVerified()).isNull();

        paymentMapper.verifyById(id);

        request = paymentMapper.findById(id);
        assertThat(request.getVerified()).isEqualTo(true);
        assertThat(request.getPaymentVerified()).isNotNull();

        log.info(request);
    }

    @Test
    @Rollback
    @Transactional
    void update() {
        final String id = UUID.randomUUID().toString();
        final String paymentMode = "wallet";

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .id(id)
                .paymentMode(paymentMode)
                .amount(new BigDecimal(1200))
                .build();

        paymentMapper.save(paymentRequest);
        PaymentRequest request = paymentMapper.findById(id);
        assertThat(request.getVerified()).isEqualTo(false);
        assertThat(request.getPaymentVerified()).isNull();
        assertThat(request.getStatus()).isNull();
        assertThat(request.getMessage()).isNull();

        final Integer status = 200;
        final String message = "success";

        request.setBalance(new BigDecimal(1500));
        request.setVerified(true);
        request.setStatus(status);
        request.setMessage(message);

        paymentMapper.update(request);

        request = paymentMapper.findById(id);
        assertThat(request.getVerified()).isEqualTo(true);
        assertThat(request.getPaymentVerified()).isNotNull();
        assertThat(request.getStatus()).isNotNull();
        assertThat(request.getMessage()).isNotNull();
        assertThat(request.getStatus()).isEqualTo(status);
        assertThat(request.getMessage()).isEqualTo(message);
        assertThat(request.getBalance().compareTo(new BigDecimal(1500))).isEqualTo(0);

        log.info(request);
    }

    @Test
    void findRefundByPaymentId() {
        final String id = "44f4be1d-ee1f-434d-8a71-5e30eaf42037";

        final Page<RefundRequest> refundByPaymentId = paymentMapper.findRefundByPaymentId(id);
        assertThat(refundByPaymentId).isNotNull();
        assertThat(refundByPaymentId.getResult().size()).isEqualTo(1);
        log.info(refundByPaymentId);
    }

    @Test
    void findRefundTotalByPaymentId() {
        final String id = "44f4be1d-ee1f-434d-8a71-5e30eaf42037";

        final Double refundTotalByPaymentId = paymentMapper.findRefundTotalByPaymentId(id);
        log.info(refundTotalByPaymentId);
    }

    @Test
    @Rollback
    @Transactional
    void saveRefundRequest() {
        final String id = UUID.randomUUID().toString();
        final String paymentId = "44f4be1d-ee1f-434d-8a71-5e30eaf42037";
        final String fundId = UUID.randomUUID().toString();

        RefundRequest refundRequest = RefundRequest.builder()
                .id(id)
                .fundRequestId(fundId)
                .refundedBy("adebola")
                .paymentId(paymentId)
                .amount(new BigDecimal(100))
                .build();

        paymentMapper.saveRefundRequest(refundRequest);
    }
}