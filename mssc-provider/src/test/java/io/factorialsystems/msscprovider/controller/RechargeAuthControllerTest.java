package io.factorialsystems.msscprovider.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.factorialsystems.msscprovider.dto.recharge.AsyncRechargeDto;
import io.factorialsystems.msscprovider.dto.recharge.SingleRechargeRequestDto;
import io.factorialsystems.msscprovider.dto.recharge.SingleRechargeResponseDto;
import io.factorialsystems.msscprovider.dto.status.MessageDto;
import io.factorialsystems.msscprovider.recharge.RechargeStatus;
import io.factorialsystems.msscprovider.service.CombinedRechargeService;
import io.factorialsystems.msscprovider.service.singlerecharge.SingleRechargeService;
import io.factorialsystems.msscprovider.utils.ProviderSecurity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(RechargeAuthController.class)
class RechargeAuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    SingleRechargeService rechargeService;

    @MockBean
    CombinedRechargeService combinedRechargeService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "spring")
    void testStartRecharge() throws Exception {
        SingleRechargeRequestDto requestDto = createRequestDto();
        SingleRechargeResponseDto responseDto = createResponseDto();

        given(rechargeService.startRecharge(any(SingleRechargeRequestDto.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/v1/auth-recharge")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(7)));

        ArgumentCaptor<SingleRechargeRequestDto> argumentCaptor = ArgumentCaptor.forClass(SingleRechargeRequestDto.class);
        verify(rechargeService).startRecharge(argumentCaptor.capture());

        assertThat(argumentCaptor.getValue().getRecipient()).isEqualTo("08055572307");
    }

    @Test
    @WithMockUser(username = "spring")
    void finishRecharge() throws Exception {
        final String id = UUID.randomUUID().toString();

        RechargeStatus rechargeStatus = createRechargeStatus();

        AsyncRechargeDto dto = AsyncRechargeDto.builder()
                .id(id)
                .email("adeomoboya@gmail.com")
                .balance(new BigDecimal(1250))
                .build();

        try (MockedStatic<ProviderSecurity> k = Mockito.mockStatic(ProviderSecurity.class)) {
            k.when(ProviderSecurity::getEmail).thenReturn("adeomoboya@gmail.com");
            k.when(ProviderSecurity::getUserName).thenReturn("adebola");

            given(rechargeService.finishRecharge(any(AsyncRechargeDto.class))).willReturn(rechargeStatus);

            mockMvc.perform(get("/api/v1/auth-recharge/" + id)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()", is(1)));

            ArgumentCaptor<AsyncRechargeDto> argumentCaptor = ArgumentCaptor.forClass(AsyncRechargeDto.class);
            verify(rechargeService).finishRecharge(argumentCaptor.capture());

            assertThat(argumentCaptor.getValue().getId()).isEqualTo(id);
        }
    }

    @Test
    @WithMockUser(username = "spring", roles = {"Onecard_Admin"})
    void retryRecharge() throws Exception {
        final String id = UUID.randomUUID().toString();
        final String recipient = "08055572307";

        RechargeStatus rechargeStatus = createRechargeStatus();

        given(rechargeService.retryRecharge(anyString(), anyString())).willReturn(rechargeStatus);

        mockMvc.perform(get("/api/v1/auth-recharge/retry/" + id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .queryParam("recipient", recipient))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> recipientCaptor = ArgumentCaptor.forClass(String.class);

        verify(rechargeService).retryRecharge(idCaptor.capture(), recipientCaptor.capture());

        assertThat(idCaptor.getValue()).isEqualTo(id);
        assertThat(recipientCaptor.getValue()).isEqualTo(recipient);
    }

    @Test
    void refundRecharge() {
        final String id = UUID.randomUUID().toString();
        given(rechargeService.refundRecharge(anyString())).willReturn(new MessageDto("Success"));

//        public MessageDto refundRecharge(String id) {
//            return singleRefundRecharge.refundRecharge(id);
//        }
    }

    @Test
    void testRefundRecharge() {
    }

    @Test
    void getUserSingleRecharges() {
    }

    @Test
    void testGetUserSingleRecharges() {
    }

    @Test
    void getSingleRequest() {
    }

    @Test
    void searchSingle() {
    }

    @Test
    void adminSearchSingle() {
    }

    @Test
    void adminSearch() {
    }

    @Test
    void getFailedTransactions() {
    }

    @Test
    void getFailedUnresolvedTransactions() {
    }

    @Test
    void generateExcelFileByUserId() {
    }

    @Test
    void generateCombinedExcelFileByUserId() {
    }

    @Test
    void generateFailedExcelFile() {
    }

    @Test
    void generateDateRangeRecharge() {
    }

    private SingleRechargeResponseDto createResponseDto() {
        return SingleRechargeResponseDto.builder()
                .id(UUID.randomUUID().toString())
                .status(200)
                .message("success")
                .amount(new BigDecimal(1200))
                .paymentMode("wallet")
                .build();
    }

    private SingleRechargeRequestDto createRequestDto() {
        SingleRechargeRequestDto requestDto = new SingleRechargeRequestDto();
        requestDto.setPaymentMode("wallet");
        requestDto.setRecipient("08055572307");
        requestDto.setServiceCode("GLO-AIRTIME");
        requestDto.setServiceCost(new BigDecimal(1200));

        return requestDto;
    }

    private RechargeStatus createRechargeStatus() {
        return RechargeStatus.builder()
                .status(HttpStatus.OK)
                .message("Success")
                .build();
    }
}