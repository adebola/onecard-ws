package io.factorialsystems.msscusers.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.serverurl}")
    private String serverUrl;

    @Value("${keycloak.master}")
    private String masterRealmName;

    @Value("${keycloak.masteruser}")
    private String masterRealmUserName;

    @Value("${keycloak.masterpassword}")
    private String masterRealmPassword;

    @Value("${keycloak.mastercli}")
    private String masterRealmCli;


    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder
                .builder()
                .serverUrl(serverUrl)
                .realm(masterRealmName)
                .username(masterRealmUserName)
                .password(masterRealmPassword)
                .clientId(masterRealmCli)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(5).build())
                .build();
    }
}
