package io.factorialsystems.msscwallet.service;

import io.factorialsystems.msscwallet.dto.DateRangeDto;
import io.factorialsystems.msscwallet.utils.K;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;

@CommonsLog
@SpringBootTest
class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    @Test
    void findUserTransactions() {
    }

    @Test
    void findOrganizationTransactionsByAccountId() {
        final String id = "13de0dc8-68e7-48fe-bb9b-e78a46fd540e";
        var x = transactionService.findOrganizationTransactionsByAccountId(id, 1 , 20);
        log.info(x.getList().size());
    }

    @Test
    void findTransaction() {
    }

    @Test
    void generateExcelTransactionFile() throws IOException {

        final String id = "e33b6988-e636-44d8-894d-c03c982d8fa5";

        try (MockedStatic<K> k = Mockito.mockStatic(K.class)) {
            k.when(K::getUserId).thenReturn(id);
            assertThat(K.getUserId()).isEqualTo(id);
            log.info(K.getUserId());

            DateRangeDto dto = new DateRangeDto();
            InputStreamResource resource = new InputStreamResource(transactionService.generateExcelTransactionFile(dto));
            File targetFile = new File("transactions.xlsx");
            OutputStream outputStream = new FileOutputStream(targetFile);
            byte[] buffer = resource.getInputStream().readAllBytes();
            outputStream.write(buffer);

            log.info(targetFile.getAbsolutePath());
        }
    }
}