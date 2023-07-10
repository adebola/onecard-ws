package io.factorialsystems.msscpayments.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscpayments.domain.PaymentRequest;
import io.factorialsystems.msscpayments.dto.PaymentRequestDto;
import io.factorialsystems.msscpayments.dto.RefundRequestDto;
import io.factorialsystems.msscpayments.dto.RefundResponseDto;
import io.factorialsystems.msscpayments.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(value = PaymentController.class)
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PaymentService paymentService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "spring")
    void getPaymentTest() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentMode("wallet");
        paymentRequest.setAmount(new BigDecimal(1000));
        paymentRequest.setId(UUID.randomUUID().toString());
        paymentRequest.setPaymentCreated(Timestamp.from(Instant.now()));
        paymentRequest.setPaymentVerified(Timestamp.from(Instant.now()));

        given(paymentService.findById(anyString())).willReturn(paymentRequest);

        final String id = UUID.randomUUID().toString();

        ArgumentCaptor<String> idArgumentCaptor = ArgumentCaptor.forClass(String.class);

        mockMvc.perform(get("/api/v1/payment/" + id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(14)));

        verify(paymentService).findById(idArgumentCaptor.capture());
        assertThat(idArgumentCaptor.getValue()).isEqualTo(id);
    }

    @Test
    @WithMockUser(username = "spring", roles = {"Onecard_Admin"})
    void testRefundPayment() throws Exception {

        final String id = UUID.randomUUID().toString();

        RefundResponseDto responseDto = RefundResponseDto.builder()
                .paymentId(UUID.randomUUID().toString())
                .message("Refund Successful")
                .status(200)
                .id(id)
                .build();

        RefundRequestDto requestDto = RefundRequestDto.builder()
                .amount(new BigDecimal(100))
                .userId("userId")
                .build();

        given(paymentService.refundPayment(anyString(), any(RefundRequestDto.class))).willReturn(responseDto);

        mockMvc.perform(put("/api/v1/payment/refund/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(4)));

        ArgumentCaptor<String> idArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<RefundRequestDto> responseArgumentCaptor = ArgumentCaptor.forClass(RefundRequestDto.class);

        verify(paymentService).refundPayment(idArgumentCaptor.capture(), responseArgumentCaptor.capture());

        assertThat(idArgumentCaptor.getValue()).isEqualTo(id);
        assertThat(responseArgumentCaptor.getValue().getUserId()).isEqualTo("userId");
    }

    @Test
    @WithMockUser(username = "spring")
    void initializePayment() throws Exception {
        PaymentRequestDto requestDto = PaymentRequestDto.builder()
                .paymentMode("wallet")
                .amount(new BigDecimal(1200))
                .build();

        given(paymentService.initializePayment(any(PaymentRequestDto.class))).willReturn(requestDto);

        mockMvc.perform(post("/api/v1/payment")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(9)));

        ArgumentCaptor<PaymentRequestDto> requestArgumentCaptor = ArgumentCaptor.forClass(PaymentRequestDto.class);
        verify(paymentService).initializePayment(requestArgumentCaptor.capture());

        assertThat(requestArgumentCaptor.getValue().getPaymentMode()).isEqualTo("wallet");
    }
}