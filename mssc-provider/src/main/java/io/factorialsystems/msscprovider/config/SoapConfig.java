package io.factorialsystems.msscprovider.config;


import io.factorialsystems.msscprovider.wsdl.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

@Configuration
public class SoapConfig {

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("io.factorialsystems.msscprovider.wsdl");
        return  marshaller;
    }

    @Bean
    ObjectFactory objectFactory() {
        return new ObjectFactory();
    }

    @Bean
    WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender();
        httpComponentsMessageSender.setReadTimeout(300000);
        httpComponentsMessageSender.setConnectionTimeout(500000);
        WebServiceTemplate template = new WebServiceTemplate(marshaller);
        template.setMessageSender(httpComponentsMessageSender);
        return template;
    }
}
