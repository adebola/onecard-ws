package io.factorialsystems.msscpayments;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "Payment Service Endpoints Management", version = "1",
        contact = @Contact(name = "OneCard", email = "care@onecardnigeria.com", url = "https://www.onecardnigeria.com"), description = "Documentation for Payment Services Endpoints v1.0"))
public class MsscPaymentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsscPaymentsApplication.class, args);
    }

}
