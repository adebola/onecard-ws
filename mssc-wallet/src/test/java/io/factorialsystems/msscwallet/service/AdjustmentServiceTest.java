package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.dto.AdjustmentRequestDto;
import io.factorialsystems.msscwallet.utils.Security;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Objects;

@SpringBootTest
@CommonsLog
class AdjustmentServiceTest {

    @Autowired
    private AdjustmentService service;

    @Test
    public void adjustBalance() {

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<Security> security  = Mockito.mockStatic(Security.class)) {
            security.when(Security::getUserId).thenReturn(id);
            assert Objects.equals(Security.getUserId(), id);
            log.info(Security.getUserId());

            AdjustmentRequestDto dto = AdjustmentRequestDto.builder()
                    .accountId("275745a4-8fb9-46f6-ac80-ff245bc62fcb")
                    .narrative("Jesus Is Lord")
                    .amount(new BigDecimal(21999))
                    .build();

            //AdjustmentResponseDto responseDto = service.adjustBalance(dto);
            //log.info(responseDto);
        }
    }

}