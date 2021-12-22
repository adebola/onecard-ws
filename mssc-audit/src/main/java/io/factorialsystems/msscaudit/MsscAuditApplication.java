package io.factorialsystems.msscaudit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class MsscAuditApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscAuditApplication.class, args);
    }
}
