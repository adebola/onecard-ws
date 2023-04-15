package io.factorialsystems.msscwallet.external.client;

import io.factorialsystems.msscwallet.dto.ServiceActionDto;
import lombok.extern.apachecommons.CommonsLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@CommonsLog
class ProviderClientTest {

    @Autowired
    private ProviderClient providerClient;

    @Test
    void getService() {
        ServiceActionDto clientService = providerClient.getService(1);
        log.info(clientService);
    }
}