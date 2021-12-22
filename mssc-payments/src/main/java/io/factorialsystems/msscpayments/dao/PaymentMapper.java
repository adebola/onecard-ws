package io.factorialsystems.msscpayments.dao;

import com.github.pagehelper.Page;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentMapper {
    Page<PaymentRequest> findAll();
    PaymentRequest findById(String id);
    void save(PaymentRequest paymentRequest);
    void verifyByReference(String reference);
    void verifyById(String id);
}
