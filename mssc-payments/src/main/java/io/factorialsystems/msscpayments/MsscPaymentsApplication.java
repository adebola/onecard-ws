package io.factorialsystems.msscpayments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsscPaymentsApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscPaymentsApplication.class, args);
    }
}
