package io.factorialsystems.msscprovider;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "Provider Service Endpoints Management", version = "1",
        contact = @Contact(name = "OneCard", email = "care@onecardnigeria.com", url = "https://www.onecardnigeria.com"), description = "Documentation for Provider Services Endpoint v1.0"))
public class MsscProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscProviderApplication.class, args);
    }
}
