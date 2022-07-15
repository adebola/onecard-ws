package io.factorialsystems.msscpayments.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.domain.RefundRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    Page<PaymentRequest> findAll();
    PaymentRequest findById(String id);
    void save(PaymentRequest paymentRequest);
    void verifyByReference(String reference);
    void verifyById(String id);
    void update(PaymentRequest request);
    Page<RefundRequest> findRefundByPaymentId(String id);
    Double findRefundTotalByPaymentId(String id);
    void saveRefundRequest(RefundRequest request);
}
