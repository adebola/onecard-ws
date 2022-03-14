package io.factorialsystems.msscprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.webservices.client.HttpWebServiceMessageSenderBuilder;
import org.springframework.boot.webservices.client.WebServiceTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.ws.client.core.WebServiceTemplate;

@EnableScheduling
@SpringBootApplication
public class MsscProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsscProviderApplication.class, args);
    }

    @Bean
    public WebServiceTemplate webServiceTemplate() {
        return new WebServiceTemplateBuilder().build();
    }
}