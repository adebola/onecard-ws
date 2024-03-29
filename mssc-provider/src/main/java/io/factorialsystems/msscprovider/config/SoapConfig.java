package io.factorialsystems.msscprovider.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapConfig {
    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("io.factorialsystems.msscprovider.wsdl");
        return  marshaller;
    }

    @Bean
    io.factorialsystems.msscprovider.wsdl.ekedp.ObjectFactory ekedpObjectFactory() {
        return new io.factorialsystems.msscprovider.wsdl.ekedp.ObjectFactory();
    }

    @Bean
    io.factorialsystems.msscprovider.wsdl.smile.ObjectFactory smileObjectFactory() {
        return new io.factorialsystems.msscprovider.wsdl.smile.ObjectFactory();

    }

    @Bean
    WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        return new WebServiceTemplate(marshaller);
    }
}
